# Firebase App Distribution Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Automatically distribute each successful tagged FreddyFridge Google Play Internal AAB to Firebase App Distribution group `owner-testers`.

**Architecture:** Preserve the existing Google Play publishing job and add a dependent Firebase-only job. The new job downloads the already verified AAB artifact, validates and decodes its dedicated credential, and invokes a focused Fastlane lane so Firebase failures can be retried without re-uploading the Play version code.

**Tech Stack:** GitHub Actions, Fastlane 2.239.0, `fastlane-plugin-firebase_app_distribution` 1.0.0, Ruby/Minitest, Firebase App Distribution.

**Spec:** `docs/release/firebase-app-distribution.md`

## Global Constraints

- Use GitHub environment `production-release`.
- Read the Firebase app ID from `vars.FIREBASE_APP_ID`; its configured value is `1:632111455840:android:76571b3604a6c14b3aed05`.
- Read the Firebase group alias from `vars.FIREBASE_APP_DISTRIBUTION_GROUPS`; its configured value is `owner-testers`.
- Read only `secrets.FIREBASE_APP_DISTRIBUTION_CREDENTIALS_BASE64` in the Firebase job.
- Do not change the existing Google Play Internal release behavior.
- Do not log or upload decoded credentials.

---

### Task 1: Specify the Firebase workflow contract

**Files:**
- Modify: `fastlane/test/android_ci_workflow_test.rb`
- Test: `fastlane/test/android_ci_workflow_test.rb`

**Interfaces:**
- Consumes: the existing `publish-internal` artifact named `google-play-internal-${{ github.ref_name }}`.
- Produces: executable expectations for a `distribute-firebase` job and its credential lifecycle.

- [x] **Step 1: Write failing workflow tests**

Add assertions that `distribute-firebase` uses the same safe tag condition, needs `publish-internal`, uses `production-release`, has `contents: read`, references exactly the Firebase credential secret and two approved variables, downloads the existing AAB artifact with pinned `actions/download-artifact@d3f86a106a0bac45b974a628896c90dbdf5c8093`, validates/decodes the JSON credential, invokes `bundle exec fastlane android distribute_firebase`, and removes the temporary JSON under `always()`.

- [x] **Step 2: Run the workflow test and verify RED**

Run: `ruby fastlane/test/android_ci_workflow_test.rb`

Expected: FAIL because job `distribute-firebase` does not exist.

- [x] **Step 3: Implement the minimal workflow job**

Add `distribute-firebase` after `publish-internal`. Download the existing artifact into `firebase-release`, validate required configuration without printing values, decode the service-account JSON into `$RUNNER_TEMP/firebase-app-distribution.json`, verify `type`, `project_id`, and `client_email`, run the Fastlane lane with the approved variables, and always delete the JSON.

- [x] **Step 4: Run the workflow test and verify GREEN**

Run: `ruby fastlane/test/android_ci_workflow_test.rb`

Expected: PASS with zero failures.

### Task 2: Add the focused Fastlane distribution lane

**Files:**
- Modify: `fastlane/test/crashlytics_configuration_test.rb`
- Modify: `fastlane/Fastfile`
- Modify: `fastlane/Gemfile`
- Modify: `fastlane/Gemfile.lock`
- Test: `fastlane/test/crashlytics_configuration_test.rb`

**Interfaces:**
- Consumes: `FIREBASE_APP_ID`, `FIREBASE_APP_DISTRIBUTION_GROUPS`, `FIREBASE_AAB_PATH`, and `GOOGLE_APPLICATION_CREDENTIALS`.
- Produces: Fastlane lane `android distribute_firebase` using the English release-notes source and the downloaded AAB.

- [x] **Step 1: Write a failing Fastlane contract test**

Assert that the Fastfile defines `lane :distribute_firebase` and calls `firebase_app_distribution` with AAB type, the four required environment inputs, and `store-assets/google-play/en-US/release-notes.txt`.

- [x] **Step 2: Run the Fastlane contract test and verify RED**

Run: `ruby fastlane/test/crashlytics_configuration_test.rb`

Expected: FAIL because the lane and plugin are absent.

- [x] **Step 3: Implement the lane and lock the plugin**

Add `gem "fastlane-plugin-firebase_app_distribution", "1.0.0"`, refresh `fastlane/Gemfile.lock`, and add the lane using `firebase_app_distribution(app:, android_artifact_type: "AAB", android_artifact_path:, groups:, release_notes_file:, service_credentials_file:)`.

- [x] **Step 4: Run the Fastlane contract test and lane discovery**

Run: `ruby fastlane/test/crashlytics_configuration_test.rb`

Run: `BUNDLE_GEMFILE=fastlane/Gemfile bundle check && BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane lanes`

Expected: both commands exit 0 and lane discovery lists `android distribute_firebase`.

### Task 3: Document and verify the complete release flow

**Files:**
- Modify: `fastlane/README.md`
- Modify: `docs/release/firebase-app-distribution.md`

**Interfaces:**
- Consumes: the implemented workflow, environment names, secret, variables, app ID, and group alias.
- Produces: operator documentation for future tags and Firebase-only retries.

- [x] **Step 1: Update release documentation**

Document automatic tester email delivery, the exact GitHub configuration names, the separate-job retry behavior, the temporary credential lifecycle, and the correct FreddyFridge Firebase app ID.

- [x] **Step 2: Run the complete verification suite**

Run: `ruby fastlane/test/release_support_test.rb && ruby fastlane/test/crashlytics_configuration_test.rb && ruby fastlane/test/android_ci_workflow_test.rb && ruby -c fastlane/release_support.rb && ruby -c fastlane/Fastfile && BUNDLE_GEMFILE=fastlane/Gemfile bundle check && BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane lanes`

Expected: every command exits 0 with no test failures or syntax errors.

- [x] **Step 3: Review the patch and commit**

Run: `git diff --check && git status --short && git diff -- .github/workflows/android-ci.yml fastlane/Fastfile fastlane/Gemfile fastlane/Gemfile.lock fastlane/test/android_ci_workflow_test.rb fastlane/test/crashlytics_configuration_test.rb fastlane/README.md docs/release/firebase-app-distribution.md docs/superpowers/plans/2026-09-16-firebase-app-distribution.md`

Commit only these files with message: `feat: distribute tagged releases with Firebase`.
