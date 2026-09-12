require "minitest/autorun"

class AndroidCiWorkflowTest < Minitest::Test
  WORKFLOW_PATH = File.expand_path("../../.github/workflows/android-ci.yml", __dir__)

  def setup
    @workflow = File.read(WORKFLOW_PATH)
  end

  def test_publishes_version_tags_after_android_quality_gates
    assert_includes @workflow, "publish-internal:"
    assert_includes @workflow, "needs: [quality, instrumentation]"
    assert_includes @workflow, "environment: google-play"
    assert_includes @workflow, "if: startsWith(github.ref, 'refs/tags/v')"
  end

  def test_passes_release_secrets_and_uses_locked_fastlane_bundle
    assert_includes @workflow, "FREDDY_UPLOAD_KEYSTORE_BASE64: \${{ secrets.FREDDY_UPLOAD_KEYSTORE_BASE64 }}"
    assert_includes @workflow, "GOOGLE_PLAY_JSON_KEY_DATA: \${{ secrets.GOOGLE_PLAY_JSON_KEY_DATA }}"
    assert_includes @workflow, "BUNDLE_GEMFILE: fastlane/Gemfile"
    assert_includes @workflow, "bundle exec fastlane android release_internal"
  end

  def test_always_removes_the_temporary_upload_keystore
    assert_includes @workflow, "if: always()"
    assert_includes @workflow, 'rm -f "$FREDDY_UPLOAD_STORE_FILE"'
  end

  def test_disposable_release_bundle_excludes_tag_refs
    assert_includes @workflow, "if: github.event_name != 'pull_request' && !startsWith(github.ref, 'refs/tags/')"
  end
end
