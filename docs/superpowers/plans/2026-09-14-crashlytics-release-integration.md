# Crashlytics Release Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enable Crashlytics-only reporting and mapping upload for protected FreddyFridge releases while provisioning Firebase configuration safely through GitHub Actions.

**Architecture:** Declare Firebase centrally through the version catalog, conditionally apply its Gradle plugins only when `app/google-services.json` exists, and keep ordinary secretless CI builds functional. Protected tag builds reconstruct and validate the ignored Firebase configuration, opt into Crashlytics mapping upload through Fastlane, then remove both Firebase and signing files unconditionally.

**Tech Stack:** Kotlin DSL, Android Gradle Plugin 8.9.1, Firebase Android BoM 34.19.0, Google Services Gradle plugin 4.5.0, Crashlytics Gradle plugin 3.0.8, Firebase Crashlytics, Ruby/Minitest, Fastlane 2.239.0, GitHub Actions.

**Spec:** `docs/superpowers/specs/2026-09-14-crashlytics-release-integration-design.md`

## Global Constraints

- Firebase clients must include exactly the required application IDs `eu.indiewalkabout.fridgemanager` and `eu.indiewalkabout.fridgemanager.testing`.
- Firebase Analytics and deprecated Firebase KTX modules must remain absent.
- Crashlytics collection is disabled for debug and enabled for release.
- Crashlytics mapping upload defaults to disabled; only the protected Fastlane store build explicitly enables it.
- `google-services.json` remains ignored and untracked; its contents and identifiers never enter logs or artifacts.
- The GitHub Environment name is exactly `production-release` and automatic publishing remains Google Play Internal with status `completed`.
- Existing tag immutability, CI quality gates, SHA-pinned publishing actions, Play credentials, and release evidence behavior remain intact.

---

### Task 1: Gradle Crashlytics integration and Firebase identity validation

**Files:**
- Modify: `.gitignore`
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `fastlane/release_support.rb`
- Modify: `fastlane/test/release_support_test.rb`
- Create: `fastlane/test/crashlytics_configuration_test.rb`

**Interfaces:**
- Produces: `FreddyRelease::Support#validate_firebase_configuration!(path)` and conditional Gradle plugin application based on `app/google-services.json`.
- Consumes: a local or CI-provisioned combined Firebase JSON containing both exact Android package IDs.

- [ ] **Step 1: Write failing Firebase identity tests**

Extend `release_support_test.rb` with temporary JSON fixtures. Assert that both required package IDs pass, while missing files, malformed JSON, and a configuration missing either release or testing client raise `FreddyRelease::ConfigurationError` without including API keys or raw JSON in the message.

```ruby
support.validate_firebase_configuration!(firebase_path)

error = assert_raises(FreddyRelease::ConfigurationError) do
  support.validate_firebase_configuration!(firebase_path_with_release_only)
end
assert_includes error.message, "eu.indiewalkabout.fridgemanager.testing"
refute_includes error.message, "current_key"
```

- [ ] **Step 2: Run the focused test and verify RED**

Run: `ruby fastlane/test/release_support_test.rb`

Expected: `NoMethodError` for `validate_firebase_configuration!`.

- [ ] **Step 3: Implement safe Firebase configuration validation**

Require Ruby JSON support, read the supplied file, extract only Android package names, require both exact IDs, and normalize file/JSON errors to `ConfigurationError`. Error messages may name the file path and missing package names but must never interpolate parsed JSON or credential values.

- [ ] **Step 4: Write failing structural Gradle and manifest tests**

Create `crashlytics_configuration_test.rb` that asserts the exact BoM/plugin versions, root plugin declarations, conditional app plugin application, BoM plus `firebase-crashlytics` dependency, absence of Analytics/KTX dependencies, manifest collection metadata, debug/release placeholder values, and a `mappingFileUploadEnabled` provider defaulting to `false`.

- [ ] **Step 5: Run the structural test and verify RED**

Run: `ruby fastlane/test/crashlytics_configuration_test.rb`

Expected: failures for the absent Firebase catalog, plugins, dependency, placeholders, and mapping configuration.

- [ ] **Step 6: Add the conditional Crashlytics Gradle integration**

Add catalog aliases for BoM `34.19.0`, Google Services `4.5.0`, Crashlytics plugin `3.0.8`, and the unversioned main Crashlytics module. Declare both plugins at root with `apply false`. In the app module, detect `app/google-services.json`, conditionally apply both plugin IDs, configure release mapping upload from `crashlyticsMappingUploadEnabled` with a strict `false` default, and add the BoM/Crashlytics dependency. Do not add Analytics.

- [ ] **Step 7: Configure build-type collection and repository hygiene**

Add manifest metadata:

```xml
<meta-data
    android:name="firebase_crashlytics_collection_enabled"
    android:value="${CRASHLYTICS_COLLECTION_ENABLED}" />
```

Set the debug placeholder to `false`, release to `true`, and add `**/google-services.json` to `.gitignore`. Confirm `app/google-services.json` disappears from `git status` and `git ls-files app/google-services.json` is empty.

- [ ] **Step 8: Run configured and secretless build verification**

Run the Ruby tests, `./gradlew assembleDebug lintDebug`, temporarily move the ignored JSON outside the project using a trap, rerun `./gradlew assembleDebug`, then restore it. Both configured and secretless debug builds must pass; the latter proves ordinary CI remains functional.

- [ ] **Step 9: Commit Task 1**

```bash
git add .gitignore gradle/libs.versions.toml build.gradle.kts app/build.gradle.kts app/src/main/AndroidManifest.xml fastlane/release_support.rb fastlane/test/release_support_test.rb fastlane/test/crashlytics_configuration_test.rb
git commit -m "feat: add Crashlytics release integration"
```

### Task 2: Protected Firebase provisioning and Fastlane mapping upload

**Files:**
- Modify: `fastlane/Fastfile`
- Modify: `.github/workflows/android-ci.yml`
- Modify: `fastlane/test/android_ci_workflow_test.rb`
- Modify: `fastlane/test/crashlytics_configuration_test.rb`

**Interfaces:**
- Consumes: `GOOGLE_SERVICES_JSON_BASE64` from GitHub Environment `production-release`.
- Produces: validated `app/google-services.json`, explicit `-PcrashlyticsMappingUploadEnabled=true`, and unconditional Firebase/keystore cleanup.

- [ ] **Step 1: Extend workflow tests and verify RED**

Update the parsed workflow tests to require environment `production-release`, exactly six secrets including `GOOGLE_SERVICES_JSON_BASE64`, a boolean-only presence check, decode to `app/google-services.json`, mode `0600`, package validation before Fastlane, and `always()` cleanup of both sensitive files. Assert the Firebase JSON is not included in release artifact paths.

Run: `ruby fastlane/test/android_ci_workflow_test.rb`

Expected: failures because the workflow still uses `google-play` and five secrets.

- [ ] **Step 2: Add a failing Fastlane mapping opt-in assertion**

Extend `crashlytics_configuration_test.rb` to require `build_release` to pass:

```ruby
properties: { "crashlyticsMappingUploadEnabled" => "true" }
```

Run the test and confirm failure before editing the Fastfile.

- [ ] **Step 3: Implement protected Firebase provisioning**

Rename the job environment, require the sixth secret, decode with `printf` piped to `base64 --decode`, set mode `0600`, and validate the JSON through the release-support helper without exposing its contents. Scope the base64 secret only to the decode step. Ensure cleanup runs with `always()` and removes both `$RUNNER_TEMP/freddy-upload.jks` and `app/google-services.json`.

- [ ] **Step 4: Enable protected mapping upload in Fastlane**

Make `validate_release` validate `app/google-services.json`. Add the exact Gradle property to `build_release`; do not change the fixed Play track/status or upload artifacts.

- [ ] **Step 5: Run focused integration verification**

Run both Ruby test files plus release-support tests, Ruby syntax, `actionlint`, YAML parsing, and `git diff --check`. Use disposable signing variables with `./gradlew bundleRelease -PcrashlyticsMappingUploadEnabled=false --dry-run` to verify the task graph locally without uploading mapping data.

- [ ] **Step 6: Commit Task 2**

```bash
git add fastlane/Fastfile .github/workflows/android-ci.yml fastlane/test/android_ci_workflow_test.rb fastlane/test/crashlytics_configuration_test.rb
git commit -m "ci: provision Firebase for Play releases"
```

### Task 3: Privacy, operations, and end-to-end local verification

**Files:**
- Modify: `fastlane/README.md`
- Modify: `docs/release/privacy-data-safety.md`
- Modify: `docs/release/production-checklist.md`

**Interfaces:**
- Produces: exact Firebase setup, secret provisioning, Crashlytics verification, privacy disclosure, and release-operation guidance.

- [ ] **Step 1: Document Firebase and GitHub setup**

Document both Firebase package IDs, Crashlytics-only/no-Analytics behavior, the `production-release` Environment, all six secrets, portable JSON base64 encoding, API-key application/signing restrictions, local ignored-file placement, and cleanup behavior.

- [ ] **Step 2: Document mapping and Console verification**

Explain that protected Fastlane builds enable mapping upload, ordinary validation builds do not, and the first tag run must be verified in Firebase Console. Include a controlled non-production test-crash procedure only as an operator step; do not add crash UI or ship intentional crash code.

- [ ] **Step 3: Update privacy and release gates**

Replace the no-crash-SDK inventory entry with Crashlytics crash/ANR and diagnostic behavior, state that no Analytics or application content/custom user ID is added, and add privacy-policy/Data Safety review plus Firebase symbolication evidence to the release checklist.

- [ ] **Step 4: Run complete verification**

Run:

```bash
ruby fastlane/test/release_support_test.rb
ruby fastlane/test/crashlytics_configuration_test.rb
ruby fastlane/test/android_ci_workflow_test.rb
ruby -c fastlane/release_support.rb
ruby -c fastlane/Fastfile
actionlint .github/workflows/android-ci.yml
ruby -e 'require "yaml"; YAML.safe_load_file(".github/workflows/android-ci.yml", aliases: true)'
./gradlew testDebugUnitTest lintDebug assembleDebug
git diff --check
git ls-files app/google-services.json
```

Expected: all tests/builds pass, actionlint and syntax checks are clean, and the final command prints nothing.

- [ ] **Step 5: Inspect the release task graph without external upload**

Create a disposable keystore under `$RUNNER_TEMP` or a validated temporary directory, export the four `FREDDY_UPLOAD_*` values, keep `-PcrashlyticsMappingUploadEnabled=false`, and run `./gradlew bundleRelease --dry-run`. Confirm the release tasks configure successfully without uploading mapping data or contacting Play.

- [ ] **Step 6: Secret and scope audit**

Search tracked files for private-key markers, service-account JSON fields, Firebase API-key values, and tracked `google-services.json`. Confirm only symbolic secret names/documentation exist and the working tree contains no unrelated changes.

- [ ] **Step 7: Commit Task 3**

```bash
git add fastlane/README.md docs/release/privacy-data-safety.md docs/release/production-checklist.md
git commit -m "docs: explain Crashlytics release operations"
```

The external completion boundary remains: add `GOOGLE_SERVICES_JSON_BASE64` to `production-release`, rename/create that GitHub Environment, then use a tagged run and Firebase Console to verify release receipt and symbolication. No Play or Firebase upload is performed during local implementation.
