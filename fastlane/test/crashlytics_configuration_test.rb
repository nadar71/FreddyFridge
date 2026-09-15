require "minitest/autorun"

class CrashlyticsConfigurationTest < Minitest::Test
  def setup
    @project_root = File.expand_path("../..", __dir__)
    @catalog = read("gradle/libs.versions.toml")
    @root_gradle = read("build.gradle.kts")
    @app_gradle = read("app/build.gradle.kts")
    @manifest = read("app/src/main/AndroidManifest.xml")
    @fastfile = read("fastlane/Fastfile")
  end

  def test_catalog_declares_exact_firebase_versions_and_crashlytics_only_modules
    assert_match(/^firebaseBom = "34\.19\.0"$/, @catalog)
    assert_match(/^googleServices = "4\.5\.0"$/, @catalog)
    assert_match(/^firebaseCrashlyticsPlugin = "3\.0\.8"$/, @catalog)
    assert_match(
      /^firebase-bom = \{ module = "com\.google\.firebase:firebase-bom", version\.ref = "firebaseBom" \}$/,
      @catalog
    )
    assert_match(
      /^firebase-crashlytics = \{ module = "com\.google\.firebase:firebase-crashlytics" \}$/,
      @catalog
    )
    assert_match(
      /^google-services = \{ id = "com\.google\.gms\.google-services", version\.ref = "googleServices" \}$/,
      @catalog
    )
    assert_match(
      /^firebase-crashlytics = \{ id = "com\.google\.firebase\.crashlytics", version\.ref = "firebaseCrashlyticsPlugin" \}$/,
      @catalog
    )
    refute_match(/firebase-(?:analytics|crashlytics-ktx)/, @catalog)
  end

  def test_root_declares_firebase_plugins_without_applying_them
    assert_match(/^\s*alias\(libs\.plugins\.google\.services\) apply false$/, @root_gradle)
    assert_match(/^\s*alias\(libs\.plugins\.firebase\.crashlytics\) apply false$/, @root_gradle)
  end

  def test_app_applies_firebase_plugins_only_when_configuration_exists
    assert_match(/val firebaseConfigurationFile = file\("google-services\.json"\)/, @app_gradle)
    assert_match(
      /if \(firebaseConfigurationFile\.isFile\) \{.*pluginManager\.apply\("com\.google\.gms\.google-services"\).*pluginManager\.apply\("com\.google\.firebase\.crashlytics"\).*\}/m,
      @app_gradle
    )
    refute_match(/^\s*(?:alias\(libs\.plugins\.(?:google\.services|firebase\.crashlytics)\)|id\("com\.google\.(?:gms\.google-services|firebase\.crashlytics)"\))\s*$/,
      @app_gradle)
  end

  def test_app_uses_firebase_bom_and_main_crashlytics_module_only
    assert_match(/^\s*implementation\(platform\(libs\.firebase\.bom\)\)$/, @app_gradle)
    assert_match(/^\s*implementation\(libs\.firebase\.crashlytics\)$/, @app_gradle)
    refute_match(/firebase-(?:analytics|crashlytics-ktx)|libs\.firebase\.(?:analytics|crashlytics\.ktx)/, @app_gradle)
  end

  def test_manifest_uses_build_type_crashlytics_collection_placeholder
    assert_match(
      /<meta-data\s+android:name="firebase_crashlytics_collection_enabled"\s+android:value="\$\{CRASHLYTICS_COLLECTION_ENABLED\}"\s*\/>/m,
      @manifest
    )
    assert_match(/debug\s*\{.*manifestPlaceholders\["CRASHLYTICS_COLLECTION_ENABLED"\] = false.*\}/m,
      @app_gradle)
    assert_match(/release\s*\{.*manifestPlaceholders\["CRASHLYTICS_COLLECTION_ENABLED"\] = true.*\}/m,
      @app_gradle)
  end

  def test_release_mapping_upload_uses_strict_boolean_provider_defaulting_to_false
    assert_match(
      /providers\.gradleProperty\("crashlyticsMappingUploadEnabled"\)\s*\.map\(String::toBooleanStrict\)\s*\.orElse\(false\)/m,
      @app_gradle
    )
    assert_match(/release\s*\{.*configure<CrashlyticsExtension>\s*\{.*mappingFileUploadEnabled = crashlyticsMappingUploadEnabled\.get\(\).*\}.*\}/m,
      @app_gradle)
  end

  def test_google_services_configuration_is_ignored
    assert_includes read(".gitignore").lines.map(&:strip), "**/google-services.json"
  end

  def test_fastlane_release_build_explicitly_enables_mapping_upload
    assert_match(
      /lane :build_release do.*gradle\(.*properties:\s*\{\s*"crashlyticsMappingUploadEnabled"\s*=>\s*"true"\s*\}.*\).*end/m,
      @fastfile
    )
  end

  private

  def read(relative_path)
    File.read(File.join(@project_root, relative_path))
  end
end
