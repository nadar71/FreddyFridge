require "minitest/autorun"
require "open3"
require "yaml"

class AndroidCiWorkflowTest < Minitest::Test
  PROJECT_ROOT = File.expand_path("../..", __dir__)
  WORKFLOW_PATH = File.expand_path("../../.github/workflows/android-ci.yml", __dir__)
  REQUIRED_SECRETS = %w[
    FREDDY_UPLOAD_KEYSTORE_BASE64
    FREDDY_UPLOAD_STORE_PASSWORD
    FREDDY_UPLOAD_KEY_ALIAS
    FREDDY_UPLOAD_KEY_PASSWORD
    GOOGLE_PLAY_JSON_KEY_DATA
    GOOGLE_SERVICES_JSON_BASE64
  ].freeze
  REQUIRED_ARTIFACT_PATHS = %w[
    app/build/outputs/bundle/release/app-release.aab
    app/build/outputs/mapping/release/mapping.txt
    app-release.aab.sha256
  ].freeze
  PINNED_ACTIONS = [
    ["actions/checkout", "11d5960a326750d5838078e36cf38b85af677262", "v4.4.0"],
    ["gradle/actions/wrapper-validation", "748248ddd2a24f49513d8f472f81c3a07d4d50e1", "v4.4.4"],
    ["actions/setup-java", "cf277c60eb25467037889841efdb72551f06f6c3", "v4.9.1"],
    ["gradle/actions/setup-gradle", "748248ddd2a24f49513d8f472f81c3a07d4d50e1", "v4.4.4"],
    ["ruby/setup-ruby", "95ef2b042f9d7a56d8268cba8559e2842e2ad01b", "v1.321.0"],
    ["actions/upload-artifact", "ea165f8d65b6e75b540449e92b4886f43607fa02", "v4.6.2"]
  ].freeze

  def setup
    @workflow = File.read(WORKFLOW_PATH)
    @configuration = YAML.safe_load(@workflow, aliases: true)
    @jobs = @configuration.fetch("jobs")
    @publish_job = @jobs.fetch("publish-internal")
    @publish_source = job_source("publish-internal")
  end

  def test_publishes_only_new_safe_tag_pushes_or_manual_matching_tag_retries
    expected_condition = <<~CONDITION.gsub(/\s+/, " ").strip
      startsWith(github.ref, 'refs/tags/v') && (
        (
          github.event_name == 'push' &&
          github.event.created == true &&
          github.event.deleted == false &&
          github.event.forced == false
        ) ||
        github.event_name == 'workflow_dispatch'
      )
    CONDITION

    assert_match(/^  workflow_dispatch:\s*$/, @workflow)
    assert_equal expected_condition, @publish_job.fetch("if").gsub(/\s+/, " ").strip
    assert_equal %w[quality instrumentation], @publish_job.fetch("needs")
    assert_equal "production-release", @publish_job.fetch("environment")
  end

  def test_has_only_read_access_and_references_exactly_six_release_secrets
    assert_equal({ "contents" => "read" }, @publish_job.fetch("permissions"))

    referenced_secrets = @publish_source.scan(/secrets\.([A-Z0-9_]+)/).flatten.uniq.sort
    assert_equal REQUIRED_SECRETS.sort, referenced_secrets
  end

  def test_checks_only_boolean_secret_presence_before_provisioning
    credential_step = named_step("Verify release credentials are configured")
    expected_environment = {
      "HAS_UPLOAD_KEYSTORE" => "${{ secrets.FREDDY_UPLOAD_KEYSTORE_BASE64 != '' }}",
      "HAS_UPLOAD_STORE_PASSWORD" => "${{ secrets.FREDDY_UPLOAD_STORE_PASSWORD != '' }}",
      "HAS_UPLOAD_KEY_ALIAS" => "${{ secrets.FREDDY_UPLOAD_KEY_ALIAS != '' }}",
      "HAS_UPLOAD_KEY_PASSWORD" => "${{ secrets.FREDDY_UPLOAD_KEY_PASSWORD != '' }}",
      "HAS_GOOGLE_PLAY_JSON_KEY_DATA" => "${{ secrets.GOOGLE_PLAY_JSON_KEY_DATA != '' }}",
      "HAS_GOOGLE_SERVICES_JSON" => "${{ secrets.GOOGLE_SERVICES_JSON_BASE64 != '' }}"
    }

    assert_equal expected_environment, credential_step.fetch("env")
    refute_match(/\$\{\{\s*secrets\./, credential_step.fetch("run"))
  end

  def test_decodes_protected_firebase_configuration_with_restricted_permissions
    decode_step = named_step("Decode Firebase configuration")

    assert_equal(
      { "GOOGLE_SERVICES_JSON_BASE64" => "${{ secrets.GOOGLE_SERVICES_JSON_BASE64 }}" },
      decode_step.fetch("env")
    )
    assert_match(
      /printf '%s' "\$GOOGLE_SERVICES_JSON_BASE64"\s*\\\s*\n\s*\| base64 --decode > app\/google-services\.json/,
      decode_step.fetch("run")
    )
    assert_includes decode_step.fetch("run"), "chmod 600 app/google-services.json"
  end

  def test_validates_firebase_packages_before_fastlane_without_secret_value_scope
    validation_step = named_step("Validate Firebase configuration")
    publish_step = named_step("Build and publish Internal release")

    assert_empty validation_step.fetch("env", {})
    assert_includes(
      validation_step.fetch("run"),
      'validate_firebase_configuration!("app/google-services.json")'
    )

    steps = @publish_job.fetch("steps")
    assert_operator steps.index(validation_step), :<, steps.index(publish_step)
    refute_includes publish_step.fetch("env"), "GOOGLE_SERVICES_JSON_BASE64"
  end

  def test_uses_locked_fastlane_bundle
    publish_step = named_step("Build and publish Internal release")

    assert_equal "fastlane/Gemfile", publish_step.fetch("env").fetch("BUNDLE_GEMFILE")
    assert_equal "bundle exec fastlane android release_internal", publish_step.fetch("run")
  end

  def test_pins_every_external_action_to_a_full_commit_sha_with_version_comment
    expected_uses = PINNED_ACTIONS.map { |action, sha, _version| "#{action}@#{sha}" }
    actual_uses = @publish_job.fetch("steps").filter_map { |step| step["uses"] }

    assert_equal expected_uses, actual_uses
    actual_uses.each do |uses|
      assert_match(%r{\A[^@]+@[0-9a-f]{40}\z}, uses)
    end
    PINNED_ACTIONS.each do |action, sha, version|
      assert_match(
        /^\s+- uses: #{Regexp.escape(action)}@#{sha} # #{Regexp.escape(version)}$/,
        @publish_source
      )
    end
  end

  def test_checks_each_release_artifact_before_upload
    verification_step = named_step("Verify release artifacts")
    verification_script = verification_step.fetch("run")

    assert_includes verification_script, '[[ ! -s "$artifact" ]]'
    REQUIRED_ARTIFACT_PATHS.each do |path|
      assert_includes verification_script, path
    end

    step_names = @publish_job.fetch("steps").map { |step| step["name"] }
    assert_operator step_names.index("Create AAB checksum"), :<, step_names.index("Verify release artifacts")
  end

  def test_uploads_all_three_release_files_for_thirty_days
    upload_step = @publish_job.fetch("steps").find do |step|
      step["uses"]&.start_with?("actions/upload-artifact@")
    end
    refute_nil upload_step

    settings = upload_step.fetch("with")
    actual_paths = settings.fetch("path").lines.map(&:strip).reject(&:empty?)
    assert_equal "google-play-internal-${{ github.ref_name }}", settings.fetch("name")
    assert_equal REQUIRED_ARTIFACT_PATHS, actual_paths
    refute_includes actual_paths, "app/google-services.json"
    assert_equal "error", settings.fetch("if-no-files-found")
    assert_equal 30, settings.fetch("retention-days")
  end

  def test_always_removes_both_temporary_release_files
    cleanup_step = named_step("Remove temporary release credentials")

    assert_equal "always()", cleanup_step.fetch("if")
    assert_includes cleanup_step.fetch("run"), 'rm -f "$RUNNER_TEMP/freddy-upload.jks"'
    assert_includes cleanup_step.fetch("run"), "rm -f app/google-services.json"
  end

  def test_ordinary_ci_jobs_do_not_reference_secrets
    ordinary_job_source = @jobs.keys.reject { |name| name == "publish-internal" }.map do |name|
      job_source(name)
    end.join

    refute_match(/secrets\./, ordinary_job_source)
  end

  def test_disposable_release_bundle_excludes_tag_refs
    assert_equal(
      "github.event_name != 'pull_request' && !startsWith(github.ref, 'refs/tags/')",
      @jobs.fetch("release-bundle").fetch("if")
    )
  end

  def test_android_resources_required_by_clean_ci_checkout_are_tracked
    required_resources = %w[
      app/src/main/res/drawable/ic_warning_white.xml
      app/src/main/res/drawable/splash_transparent_icon.xml
    ]

    output, status = Open3.capture2e(
      "git", "-C", PROJECT_ROOT, "ls-files", "--error-unmatch", *required_resources
    )

    assert status.success?, "Required Android resources are not tracked:\n#{output}"
  end

  private

  def named_step(name)
    @publish_job.fetch("steps").find { |step| step["name"] == name } ||
      flunk("Missing publish-internal step named #{name.inspect}")
  end

  def job_source(name)
    lines = @workflow.lines
    start_index = lines.index { |line| line.match?(/^  #{Regexp.escape(name)}:\s*$/) }
    raise "Missing job #{name.inspect}" unless start_index

    end_index = ((start_index + 1)...lines.length).find do |index|
      lines[index].match?(/^  [a-zA-Z0-9_-]+:\s*$/)
    end
    lines[start_index...(end_index || lines.length)].join
  end
end
