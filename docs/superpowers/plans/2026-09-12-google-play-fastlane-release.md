# Google Play Fastlane Release Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Automatically build, sign, and upload a tagged FreddyFridge release to Google Play Internal testing after all existing CI gates pass.

**Architecture:** Keep `.github/workflows/android-ci.yml` as the single quality and release pipeline. Put deterministic version/metadata validation in a small dependency-free Ruby helper used by Fastlane, then let a tag-only GitHub Actions job reconstruct ephemeral credentials, invoke a fixed Internal-track lane, archive release evidence, and clean up the keystore.

**Tech Stack:** Ruby 3.3, Fastlane 2.239.0, Minitest from the Ruby standard library, Gradle 8.11.1, GitHub Actions, Google Play Developer API.

**Spec:** `docs/superpowers/specs/2026-09-11-google-play-fastlane-release-design.md`

## Global Constraints

- A pushed tag must be exactly `v<versionName>` from `app/build.gradle.kts`.
- Automated publication is fixed to Google Play track `internal` and release status `completed`.
- The package name is `eu.indiewalkabout.fridgemanager`.
- Existing unit, lint, debug-build, API 26, and API 36 instrumentation gates must pass before publication.
- Store metadata locales are exactly `en-US` and `it-IT`.
- Credentials may exist only in GitHub Environment secrets, environment variables, or `$RUNNER_TEMP`; they must never enter logs or artifacts.
- Preserve all unrelated uncommitted Gradle and image changes.

---

### Task 1: Testable release validation and metadata synchronization

**Files:**
- Create: `fastlane/release_support.rb`
- Create: `fastlane/test/release_support_test.rb`
- Modify: `fastlane/Fastfile`

**Interfaces:**
- Produces: `FreddyRelease::Support.new(project_root:)`, `#version`, `#validate_tag!(tag)`, `#validate_credentials!(environment)`, and `#sync_store_assets!`.
- Consumes: `app/build.gradle.kts`, `store-assets/google-play/{en-US,it-IT}`, and a hash-like environment containing Google Play and signing variables.

- [ ] **Step 1: Write failing unit tests for version parsing and tag validation**

Create `fastlane/test/release_support_test.rb` with temporary project fixtures and assertions that parsing returns `{ code: "13", name: "2.1.0" }`, `v2.1.0` passes, and `v2.1.1` raises `FreddyRelease::ConfigurationError` containing both the tag and expected version.

```ruby
def test_reads_version_and_accepts_matching_tag
  write_gradle('versionCode = 13\nversionName = "2.1.0"\n')
  support = FreddyRelease::Support.new(project_root: @root)

  assert_equal({ code: "13", name: "2.1.0" }, support.version)
  support.validate_tag!("v2.1.0")
end

def test_rejects_tag_that_does_not_match_version_name
  write_gradle('versionCode = 13\nversionName = "2.1.0"\n')
  error = assert_raises(FreddyRelease::ConfigurationError) do
    FreddyRelease::Support.new(project_root: @root).validate_tag!("v2.1.1")
  end

  assert_includes error.message, "v2.1.1"
  assert_includes error.message, "v2.1.0"
end
```

- [ ] **Step 2: Run the focused tests and verify they fail**

Run: `ruby fastlane/test/release_support_test.rb`

Expected: `LoadError` because `fastlane/release_support.rb` does not exist.

- [ ] **Step 3: Implement version parsing, exact tag validation, and credential validation**

Implement a dependency-free `FreddyRelease::Support` class. It must raise `ConfigurationError` for an unreadable/malformed Gradle version, a mismatched or blank tag, or any blank required credential. Required credential keys are:

```ruby
%w[
  FREDDY_UPLOAD_STORE_FILE
  FREDDY_UPLOAD_STORE_PASSWORD
  FREDDY_UPLOAD_KEY_ALIAS
  FREDDY_UPLOAD_KEY_PASSWORD
  GOOGLE_PLAY_JSON_KEY_DATA
]
```

- [ ] **Step 4: Add failing metadata synchronization tests**

Add tests that create EN/IT listing, release-note, icon, feature-graphic, and screenshot fixtures; call `sync_store_assets!`; then assert exact title, short description, full description, copied images, and `<versionCode>.txt` output. Add one negative test asserting that a missing `release-notes.txt` raises an error naming the path.

- [ ] **Step 5: Run the expanded tests and verify the metadata cases fail**

Run: `ruby fastlane/test/release_support_test.rb`

Expected: version/tag tests pass and metadata tests fail because `sync_store_assets!` is absent.

- [ ] **Step 6: Implement strict metadata synchronization**

Implement synchronization for exactly `en-US` and `it-IT`. Parse the localized listing headings, reject missing or blank text/assets/notes, require at least one PNG screenshot, copy only PNG screenshots in sorted order, and write Fastlane changelogs using the parsed version code.

- [ ] **Step 7: Refactor the Fastfile to use the helper**

Make `validate_release` call `validate_tag!` and `validate_credentials!`, make `sync_store_assets` delegate to the helper, retain `build_release`, and make `release_internal` execute validation, synchronization, the signed bundle build, and:

```ruby
upload_to_play_store(
  package_name: "eu.indiewalkabout.fridgemanager",
  aab: File.join(PROJECT_ROOT, "app/build/outputs/bundle/release/app-release.aab"),
  track: "internal",
  release_status: "completed",
  skip_upload_apk: true,
  json_key_data: ENV.fetch("GOOGLE_PLAY_JSON_KEY_DATA"),
  timeout: 900
)
```

Use absolute project-root paths and configure the Gradle action with the repository root so invocation is independent of the current working directory.

- [ ] **Step 8: Run Ruby tests and syntax checks**

Run: `ruby fastlane/test/release_support_test.rb && ruby -c fastlane/release_support.rb && ruby -c fastlane/Fastfile`

Expected: all tests pass and both syntax checks print `Syntax OK`.

- [ ] **Step 9: Commit the validation unit**

```bash
git add fastlane/release_support.rb fastlane/test/release_support_test.rb fastlane/Fastfile
git commit -m "feat: validate Play release inputs"
```

### Task 2: Reproducible Fastlane dependency and local interface

**Files:**
- Modify: `fastlane/Gemfile`
- Create: `fastlane/Gemfile.lock`
- Modify: `fastlane/Appfile`

**Interfaces:**
- Consumes: Ruby 3.3 and Bundler.
- Produces: reproducible `BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane android <lane>` commands.

- [ ] **Step 1: Pin Fastlane and generate the lockfile**

Set `fastlane/Gemfile` to:

```ruby
source "https://rubygems.org"

gem "fastlane", "2.239.0"
```

Run: `BUNDLE_GEMFILE=fastlane/Gemfile bundle lock`

Expected: `fastlane/Gemfile.lock` resolves Fastlane 2.239.0 with no dependency error.

- [ ] **Step 2: Keep Appfile configuration explicit and secret-free**

Set `fastlane/Appfile` to:

```ruby
package_name "eu.indiewalkabout.fridgemanager"
```

Do not add JSON key paths or account identifiers.

- [ ] **Step 3: Install dependencies and discover lanes**

Run: `BUNDLE_GEMFILE=fastlane/Gemfile bundle install && BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane lanes`

Expected: Bundler succeeds and output lists `android build_release`, `android sync_store_assets`, `android validate_release`, and `android release_internal`.

- [ ] **Step 4: Commit dependency configuration**

```bash
git add fastlane/Gemfile fastlane/Gemfile.lock fastlane/Appfile
git commit -m "build: pin Fastlane release tooling"
```

### Task 3: CI-gated tag publishing job

**Files:**
- Create: `fastlane/test/android_ci_workflow_test.rb`
- Modify: `.github/workflows/android-ci.yml`

**Interfaces:**
- Consumes: tags matching `v*`, the `quality` and `instrumentation` jobs, the `google-play` GitHub Environment, and its five documented secrets.
- Produces: a completed Google Play Internal testing release and a `google-play-internal-<tag>` artifact containing the signed AAB, R8 mapping, and SHA-256 checksum.

- [ ] **Step 1: Write failing structural workflow tests**

Create a dependency-free Minitest file that reads `.github/workflows/android-ci.yml` as text and asserts:

```ruby
assert_includes workflow, "publish-internal:"
assert_includes workflow, "needs: [quality, instrumentation]"
assert_includes workflow, "environment: google-play"
assert_includes workflow, "if: startsWith(github.ref, 'refs/tags/v')"
assert_includes workflow, "FREDDY_UPLOAD_KEYSTORE_BASE64: \${{ secrets.FREDDY_UPLOAD_KEYSTORE_BASE64 }}"
assert_includes workflow, "GOOGLE_PLAY_JSON_KEY_DATA: \${{ secrets.GOOGLE_PLAY_JSON_KEY_DATA }}"
assert_includes workflow, "BUNDLE_GEMFILE: fastlane/Gemfile"
assert_includes workflow, "bundle exec fastlane android release_internal"
assert_includes workflow, "if: always()"
assert_includes workflow, 'rm -f "$FREDDY_UPLOAD_STORE_FILE"'
```

Also assert the disposable `release-bundle` condition excludes tag refs.

- [ ] **Step 2: Run the workflow test and verify it fails**

Run: `ruby fastlane/test/android_ci_workflow_test.rb`

Expected: failure because `publish-internal` is absent.

- [ ] **Step 3: Add the tag-only publishing job**

Modify `.github/workflows/android-ci.yml` so `release-bundle` runs only for non-PR, non-tag events. Add `publish-internal` with:

- `if: startsWith(github.ref, 'refs/tags/v')`
- `needs: [quality, instrumentation]`
- `environment: google-play`
- `permissions: { contents: read }`
- checkout, wrapper validation, Temurin Java 17, Gradle setup, and `ruby/setup-ruby@v1` with Ruby 3.3 and Bundler caching from `fastlane/Gemfile.lock`
- a credential-presence check that does not echo secret values
- base64 decoding to `$RUNNER_TEMP/freddy-upload.jks`
- signing and Play credentials exposed as environment variables only for the Fastlane step
- `BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane android release_internal`
- `sha256sum app/build/outputs/bundle/release/app-release.aab > app-release.aab.sha256`
- artifact upload with a 30-day retention
- an `always()` cleanup step removing the temporary keystore

- [ ] **Step 4: Run workflow structural and Ruby tests**

Run: `ruby fastlane/test/android_ci_workflow_test.rb && ruby fastlane/test/release_support_test.rb`

Expected: all tests pass.

- [ ] **Step 5: Commit workflow automation**

```bash
git add .github/workflows/android-ci.yml fastlane/test/android_ci_workflow_test.rb
git commit -m "ci: publish version tags to Play Internal"
```

### Task 4: Operator documentation and complete verification

**Files:**
- Modify: `fastlane/README.md`
- Modify: `docs/release/production-checklist.md`

**Interfaces:**
- Consumes: repository administrator access, a Play Console service account, and the protected upload key.
- Produces: exact one-time setup, tagging, monitoring, retry, and promotion instructions.

- [ ] **Step 1: Document one-time GitHub and Play setup**

Document creation of the `google-play` GitHub Environment with no required reviewer, all five secret names, a portable keystore encoding command, least-privilege Play Console service-account access, and the requirement that the first app release/Play App Signing setup may need completion in Play Console.

- [ ] **Step 2: Document release operation and recovery**

Document the pre-tag version/release-note updates, local validation commands, annotated `v<versionName>` tag creation, automatic CI gates, expected Internal testing result, artifact evidence, and the rule that an accepted Play version code cannot be reused.

- [ ] **Step 3: Align the production checklist**

Update the Internal testing checklist entry to reference the tag-triggered workflow and its archived checksum. Keep production staged rollout manual and explicitly outside the automated lane.

- [ ] **Step 4: Run the full local verification suite**

Run:

```bash
ruby fastlane/test/release_support_test.rb
ruby fastlane/test/android_ci_workflow_test.rb
ruby -c fastlane/release_support.rb
ruby -c fastlane/Fastfile
BUNDLE_GEMFILE=fastlane/Gemfile bundle check
BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane lanes
./gradlew testDebugUnitTest lintDebug
git diff --check
```

Expected: Ruby tests pass, syntax is valid, dependencies are satisfied, all lanes are listed, Gradle tests/lint pass, and `git diff --check` reports no whitespace errors.

- [ ] **Step 5: Review secret handling and unrelated changes**

Run:

```bash
rg -n "BEGIN (RSA )?PRIVATE KEY|private_key|client_email|storePassword|keyPassword" .github fastlane docs/release
git status --short
git diff -- app/build.gradle.kts
```

Expected: only secret names and documentation placeholders are present; no credential values exist; the pre-existing debug application-ID diff and image changes remain untouched.

- [ ] **Step 6: Commit operator documentation**

```bash
git add fastlane/README.md docs/release/production-checklist.md
git commit -m "docs: explain automated Play releases"
```

- [ ] **Step 7: Record the external verification boundary**

Report that no real Play upload was attempted locally. The first end-to-end verification is a matching version-tag push after the GitHub Environment, secrets, Play Console service account, Play App Signing, and initial application setup are complete.
