require "fileutils"
require "json"
require "minitest/autorun"
require "tmpdir"

require_relative "../release_support"

class ReleaseSupportTest < Minitest::Test
  def setup
    @root = Dir.mktmpdir("freddy-release")
  end

  def teardown
    FileUtils.remove_entry(@root)
  end

  def test_reads_version_and_accepts_matching_tag
    write_gradle("versionCode = 13\nversionName = \"2.1.0\"\n")
    support = FreddyRelease::Support.new(project_root: @root)

    assert_equal({ code: "13", name: "2.1.0" }, support.version)
    support.validate_tag!("v2.1.0")
  end

  def test_rejects_tag_that_does_not_match_version_name
    write_gradle("versionCode = 13\nversionName = \"2.1.0\"\n")

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).validate_tag!("v2.1.1")
    end

    assert_includes error.message, "v2.1.1"
    assert_includes error.message, "v2.1.0"
  end

  def test_rejects_blank_release_tag
    write_gradle("versionCode = 13\nversionName = \"2.1.0\"\n")

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).validate_tag!("  ")
    end

    assert_includes error.message, "v2.1.0"
  end

  def test_rejects_malformed_gradle_version
    write_gradle("versionCode = thirteen\nversionName = \"2.1.0\"\n")

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).version
    end

    assert_includes error.message, "Malformed version configuration"
  end

  def test_wraps_unreadable_gradle_file_errors
    gradle_file = File.join(@root, "app", "build.gradle.kts")
    FileUtils.mkdir_p(gradle_file)

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).version
    end

    assert_includes error.message, "Unable to read Gradle version file"
    assert_includes error.message, "app/build.gradle.kts"
  end

  def test_rejects_blank_required_credentials
    environment = {
      "FREDDY_UPLOAD_STORE_FILE" => "/tmp/upload.jks",
      "FREDDY_UPLOAD_STORE_PASSWORD" => "store-password",
      "FREDDY_UPLOAD_KEY_ALIAS" => "upload",
      "FREDDY_UPLOAD_KEY_PASSWORD" => "key-password",
      "GOOGLE_PLAY_JSON_KEY_DATA" => "{\"type\":\"service_account\"}"
    }
    environment["GOOGLE_PLAY_JSON_KEY_DATA"] = "  "

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).validate_credentials!(environment)
    end

    assert_includes error.message, "GOOGLE_PLAY_JSON_KEY_DATA"
  end

  def test_accepts_firebase_configuration_with_both_required_clients_and_additional_clients
    firebase_path = write_firebase_configuration(
      "com.example.unrelated",
      "eu.indiewalkabout.fridgemanager.testing",
      "eu.indiewalkabout.fridgemanager"
    )

    FreddyRelease::Support.new(project_root: @root).validate_firebase_configuration!(firebase_path)
  end

  def test_rejects_missing_firebase_configuration
    firebase_path = File.join(@root, "missing-google-services.json")

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).validate_firebase_configuration!(firebase_path)
    end

    assert_includes error.message, firebase_path
  end

  def test_rejects_malformed_firebase_configuration_without_leaking_contents
    firebase_path = File.join(@root, "google-services.json")
    File.write(firebase_path, "{\"current_key\":\"SYNTHETIC_SECRET\",")

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).validate_firebase_configuration!(firebase_path)
    end

    assert_includes error.message, firebase_path
    refute_includes error.message, "SYNTHETIC_SECRET"
    refute_includes error.message, "current_key"
  end

  def test_rejects_firebase_configuration_missing_release_client_without_leaking_credentials
    firebase_path = write_firebase_configuration("eu.indiewalkabout.fridgemanager.testing")

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).validate_firebase_configuration!(firebase_path)
    end

    assert_includes error.message, "eu.indiewalkabout.fridgemanager"
    refute_includes error.message, "SYNTHETIC_SECRET"
    refute_includes error.message, "current_key"
  end

  def test_rejects_firebase_configuration_missing_testing_client_without_leaking_credentials
    firebase_path = write_firebase_configuration("eu.indiewalkabout.fridgemanager")

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).validate_firebase_configuration!(firebase_path)
    end

    assert_includes error.message, "eu.indiewalkabout.fridgemanager.testing"
    refute_includes error.message, "SYNTHETIC_SECRET"
    refute_includes error.message, "current_key"
  end

  def test_syncs_localized_metadata_images_and_changelogs
    write_gradle("versionCode = 13\nversionName = \"2.1.0\"\n")
    write_store_assets(
      "en-US",
      listing: "App name\nFreddyFridge EN\n\nShort description\nEnglish short description\n\nFull description\nEnglish full description.\n",
      release_notes: "English release notes"
    )
    write_store_assets(
      "it-IT",
      listing: "Nome app\nFreddyFridge IT\n\nDescrizione breve\nDescrizione più chiara\n\nDescrizione completa\nDescrizione completa italiana.\n",
      release_notes: "Novità dell'app"
    )

    FreddyRelease::Support.new(project_root: @root).sync_store_assets!

    assert_metadata(
      "en-US",
      title: "FreddyFridge EN",
      short_description: "English short description",
      full_description: "English full description.",
      release_notes: "English release notes"
    )
    assert_metadata(
      "it-IT",
      title: "FreddyFridge IT",
      short_description: "Descrizione più chiara",
      full_description: "Descrizione completa italiana.",
      release_notes: "Novità dell'app"
    )
  end

  def test_rejects_missing_release_notes_with_its_path
    write_gradle("versionCode = 13\nversionName = \"2.1.0\"\n")
    write_store_assets(
      "en-US",
      listing: "App name\nFreddyFridge EN\n\nShort description\nEnglish short description\n\nFull description\nEnglish full description.\n",
      release_notes: "English release notes"
    )
    write_store_assets(
      "it-IT",
      listing: "Nome app\nFreddyFridge IT\n\nDescrizione breve\nDescrizione breve italiana\n\nDescrizione completa\nDescrizione completa italiana.\n",
      release_notes: nil
    )

    error = assert_raises(FreddyRelease::ConfigurationError) do
      FreddyRelease::Support.new(project_root: @root).sync_store_assets!
    end

    assert_includes error.message, "store-assets/google-play/it-IT/release-notes.txt"
  end

  private

  def write_gradle(content)
    gradle_file = File.join(@root, "app", "build.gradle.kts")
    FileUtils.mkdir_p(File.dirname(gradle_file))
    File.write(gradle_file, content)
  end

  def write_firebase_configuration(*package_names)
    firebase_path = File.join(@root, "google-services.json")
    clients = package_names.map do |package_name|
      {
        "client_info" => {
          "android_client_info" => {
            "package_name" => package_name
          }
        },
        "api_key" => [{ "current_key" => "SYNTHETIC_SECRET" }]
      }
    end
    File.write(firebase_path, JSON.generate("client" => clients))
    firebase_path
  end

  def write_store_assets(locale, listing:, release_notes:)
    source = File.join(@root, "store-assets", "google-play", locale)
    FileUtils.mkdir_p(File.join(source, "phone-screenshots"))
    File.write(File.join(source, "listing.txt"), listing)
    File.write(File.join(source, "release-notes.txt"), release_notes) if release_notes
    File.binwrite(File.join(source, "feature-graphic-1024x500.png"), "feature-#{locale}")
    File.binwrite(File.join(source, "phone-screenshots", "02-second.png"), "second-#{locale}")
    File.binwrite(File.join(source, "phone-screenshots", "01-first.png"), "first-#{locale}")
    File.binwrite(File.join(source, "phone-screenshots", "ignored.jpg"), "ignored-#{locale}")

    shared = File.join(@root, "store-assets", "google-play", "shared")
    FileUtils.mkdir_p(shared)
    File.binwrite(File.join(shared, "icon-512.png"), "shared-icon")
  end

  def assert_metadata(locale, title:, short_description:, full_description:, release_notes:)
    target = File.join(@root, "fastlane", "metadata", "android", locale)

    assert_equal "#{title}\n", File.read(File.join(target, "title.txt"), encoding: Encoding::UTF_8)
    assert_equal "#{short_description}\n", File.read(File.join(target, "short_description.txt"), encoding: Encoding::UTF_8)
    assert_equal "#{full_description}\n", File.read(File.join(target, "full_description.txt"), encoding: Encoding::UTF_8)
    assert_equal "shared-icon", File.binread(File.join(target, "images", "icon.png"))
    assert_equal "feature-#{locale}", File.binread(File.join(target, "images", "featureGraphic.png"))
    assert_equal %w[01-first.png 02-second.png], Dir.children(File.join(target, "images", "phoneScreenshots")).sort
    assert_equal "first-#{locale}", File.binread(File.join(target, "images", "phoneScreenshots", "01-first.png"))
    assert_equal "second-#{locale}", File.binread(File.join(target, "images", "phoneScreenshots", "02-second.png"))
    assert_equal "#{release_notes}\n",
      File.read(File.join(target, "changelogs", "13.txt"), encoding: Encoding::UTF_8)
  end
end
