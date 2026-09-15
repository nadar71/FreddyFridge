require "fileutils"
require "json"

module FreddyRelease
  class ConfigurationError < StandardError; end

  class Support
    LOCALES = {
      "en-US" => {
        title_heading: "App name",
        short_description_heading: "Short description",
        full_description_heading: "Full description"
      },
      "it-IT" => {
        title_heading: "Nome app",
        short_description_heading: "Descrizione breve",
        full_description_heading: "Descrizione completa"
      }
    }.freeze

    REQUIRED_CREDENTIAL_KEYS = %w[
      FREDDY_UPLOAD_STORE_FILE
      FREDDY_UPLOAD_STORE_PASSWORD
      FREDDY_UPLOAD_KEY_ALIAS
      FREDDY_UPLOAD_KEY_PASSWORD
      GOOGLE_PLAY_JSON_KEY_DATA
    ].freeze

    REQUIRED_FIREBASE_PACKAGE_NAMES = %w[
      eu.indiewalkabout.fridgemanager
      eu.indiewalkabout.fridgemanager.testing
    ].freeze

    def initialize(project_root:)
      @project_root = project_root
    end

    def version
      gradle_file = File.join(@project_root, "app", "build.gradle.kts")
      content = File.read(gradle_file)
      code = content.match(/^\s*versionCode\s*=\s*(\d+)\s*$/)&.[](1)
      name = content.match(/^\s*versionName\s*=\s*"([^"]+)"\s*$/)&.[](1)

      unless code && name && !name.strip.empty?
        raise ConfigurationError, "Malformed version configuration in #{gradle_file}"
      end

      { code: code, name: name }
    rescue SystemCallError, IOError => error
      raise ConfigurationError, "Unable to read Gradle version file #{gradle_file}: #{error.message}"
    end

    def validate_tag!(tag)
      expected_tag = "v#{version[:name]}"

      unless tag && !tag.strip.empty? && tag == expected_tag
        raise ConfigurationError, "Release tag #{tag.inspect} must match #{expected_tag}"
      end
    end

    def validate_credentials!(environment)
      missing_key = REQUIRED_CREDENTIAL_KEYS.find do |key|
        value = environment[key]
        value.nil? || value.strip.empty?
      end

      if missing_key
        raise ConfigurationError, "Missing required release credential: #{missing_key}"
      end
    end

    def validate_firebase_configuration!(path)
      configuration = JSON.parse(File.read(path))
      clients = configuration.is_a?(Hash) && configuration["client"].is_a?(Array) ? configuration["client"] : []
      package_names = clients.filter_map do |client|
        next unless client.is_a?(Hash)

        client_info = client["client_info"]
        next unless client_info.is_a?(Hash)

        android_client_info = client_info["android_client_info"]
        next unless android_client_info.is_a?(Hash)

        android_client_info["package_name"]
      end
      missing_package_names = REQUIRED_FIREBASE_PACKAGE_NAMES - package_names

      unless missing_package_names.empty?
        raise ConfigurationError,
          "Firebase configuration #{path} is missing Android client(s): #{missing_package_names.join(", ")}"
      end
    rescue JSON::ParserError
      raise ConfigurationError, "Malformed Firebase configuration file: #{path}"
    rescue SystemCallError, IOError
      raise ConfigurationError, "Unable to read Firebase configuration file: #{path}"
    end

    def sync_store_assets!
      version_code = version.fetch(:code)

      LOCALES.each do |locale, headings|
        sync_locale(locale, headings, version_code)
      end
    end

    private

    def sync_locale(locale, headings, version_code)
      source = File.join(@project_root, "store-assets", "google-play", locale)
      target = File.join(@project_root, "fastlane", "metadata", "android", locale)
      listing = read_required_text(File.join(source, "listing.txt"))
      metadata = parse_listing(listing, headings, source)
      release_notes = read_required_text(File.join(source, "release-notes.txt"))
      icon = read_required_asset(File.join(@project_root, "store-assets", "google-play", "shared", "icon-512.png"))
      feature_graphic = read_required_asset(File.join(source, "feature-graphic-1024x500.png"))
      screenshots = png_screenshots(File.join(source, "phone-screenshots"))

      FileUtils.mkdir_p(target)
      File.write(File.join(target, "title.txt"), "#{metadata.fetch(:title)}\n")
      File.write(File.join(target, "short_description.txt"), "#{metadata.fetch(:short_description)}\n")
      File.write(File.join(target, "full_description.txt"), "#{metadata.fetch(:full_description)}\n")

      image_dir = File.join(target, "images")
      FileUtils.mkdir_p(image_dir)
      File.binwrite(File.join(image_dir, "icon.png"), icon)
      File.binwrite(File.join(image_dir, "featureGraphic.png"), feature_graphic)

      screenshot_target = File.join(image_dir, "phoneScreenshots")
      FileUtils.rm_rf(screenshot_target)
      FileUtils.mkdir_p(screenshot_target)
      screenshots.each do |screenshot|
        FileUtils.cp(screenshot, File.join(screenshot_target, File.basename(screenshot)))
      end

      changelog_dir = File.join(target, "changelogs")
      FileUtils.mkdir_p(changelog_dir)
      File.write(File.join(changelog_dir, "#{version_code}.txt"), "#{release_notes}\n")
    end

    def parse_listing(listing, headings, source)
      lines = listing.lines(chomp: true)

      {
        title: value_after_heading(lines, headings.fetch(:title_heading), source),
        short_description: value_after_heading(lines, headings.fetch(:short_description_heading), source),
        full_description: value_after_heading(lines, headings.fetch(:full_description_heading), source, remaining: true)
      }
    end

    def value_after_heading(lines, heading, source, remaining: false)
      heading_index = lines.index(heading)
      value = if heading_index
                values = lines[(heading_index + 1)..]
                remaining ? values.join("\n").strip : values.first.to_s.strip
              end

      if value.nil? || value.empty?
        raise ConfigurationError, "Missing or blank #{heading.inspect} in #{relative_path(source)}/listing.txt"
      end

      value
    end

    def png_screenshots(directory)
      unless Dir.exist?(directory)
        raise ConfigurationError, "Missing screenshot directory: #{relative_path(directory)}"
      end

      screenshots = Dir.children(directory)
        .select { |filename| File.extname(filename).downcase == ".png" }
        .sort
        .map { |filename| File.join(directory, filename) }

      if screenshots.empty?
        raise ConfigurationError, "Missing PNG screenshots in #{relative_path(directory)}"
      end

      screenshots.each { |screenshot| read_required_asset(screenshot) }
      screenshots
    end

    def read_required_text(path)
      content = File.read(path, encoding: Encoding::UTF_8).strip
      if content.empty?
        raise ConfigurationError, "Missing or blank required text file: #{relative_path(path)}"
      end

      content
    rescue Errno::ENOENT, Errno::EACCES => error
      raise ConfigurationError, "Unable to read required text file #{relative_path(path)}: #{error.message}"
    end

    def read_required_asset(path)
      content = File.binread(path)
      if content.empty?
        raise ConfigurationError, "Missing or blank required asset: #{relative_path(path)}"
      end

      content
    rescue Errno::ENOENT, Errno::EACCES => error
      raise ConfigurationError, "Unable to read required asset #{relative_path(path)}: #{error.message}"
    end

    def relative_path(path)
      path.delete_prefix("#{@project_root}/")
    end
  end
end
