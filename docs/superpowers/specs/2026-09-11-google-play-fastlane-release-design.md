# Google Play Fastlane Release Design

## Goal

Publish a production-signed FreddyFridge Android App Bundle automatically to
Google Play Internal testing whenever a version tag such as `v2.1.0` is pushed.
Publication must happen only after the repository's existing automated quality
and instrumentation gates pass.

## Scope

This change configures Fastlane and GitHub Actions for Internal testing uploads.
It does not promote releases to closed, open, or production tracks, create Git
tags, increment application versions, configure the Play Console, or create
signing and service-account credentials.

## Release trigger and gates

The existing `.github/workflows/android-ci.yml` remains the single CI and
release workflow. Its existing `v*` tag trigger starts the release process.

For a tag build, GitHub Actions will:

1. Run the existing unit-test, lint, debug-build, and API 26/API 36
   instrumentation jobs.
2. Validate that the tag is exactly `v<versionName>` from
   `app/build.gradle.kts` and that `versionCode` and localized changelog files
   are present.
3. Reconstruct the protected upload keystore in the runner's temporary
   directory and expose its path and passwords only through job environment
   variables.
4. Invoke the Fastlane Android release lane, which builds a minified signed AAB
   and uploads it with store metadata to the Google Play `internal` track with
   release status `completed`.
5. Generate a SHA-256 checksum and archive the AAB, R8 mapping, and checksum as
   GitHub Actions artifacts.

The current disposable-key release build remains available for non-tag pushes
to `develop`. It will not run for version tags because the tag publishing job
builds the real signed artifact instead.

## Fastlane structure

The checked-in `fastlane/` directory is the canonical publishing interface.

- `Gemfile` pins Fastlane to a repository-reviewed compatible version and
  generates a committed lockfile.
- `Appfile` defines the package name
  `eu.indiewalkabout.fridgemanager`.
- `Fastfile` provides focused lanes for metadata synchronization, signed bundle
  construction, release validation, and Internal testing publication.
- `fastlane/metadata/android/` contains the generated Play metadata consumed by
  `upload_to_play_store`.
- `fastlane/README.md` documents local use, GitHub secrets, Play Console setup,
  tag creation, and failure recovery.

The automated release lane has a fixed `internal` track. A future production
promotion must be a separate reviewed change and must follow
`docs/release/production-checklist.md`.

## Version and metadata contract

Before publishing, the release tooling reads `versionName` and `versionCode`
from `app/build.gradle.kts`. The pushed tag must equal `v<versionName>`; for
example, `versionName = "2.1.0"` requires tag `v2.1.0`.

Store metadata is sourced from `store-assets/google-play/en-US` and
`store-assets/google-play/it-IT`. Synchronization must fail with an actionable
message when a required listing, graphic, screenshot directory, or release-note
file is missing. The release notes are copied to each locale's Fastlane
changelog named `<versionCode>.txt`.

## Secrets and permissions

The publishing job uses a GitHub Environment named `google-play`. The
environment should have no required reviewer so tag publishing remains
automatic, but repository administrators may add branch or tag protection.
The workflow receives these environment secrets:

- `FREDDY_UPLOAD_KEYSTORE_BASE64`: base64-encoded upload keystore bytes.
- `FREDDY_UPLOAD_STORE_PASSWORD`: upload keystore password.
- `FREDDY_UPLOAD_KEY_ALIAS`: upload key alias.
- `FREDDY_UPLOAD_KEY_PASSWORD`: upload key password.
- `GOOGLE_PLAY_JSON_KEY_DATA`: Google Play service-account JSON payload.

The job has only `contents: read` repository permission. It decodes the
keystore under `$RUNNER_TEMP`, masks the temporary path where useful, and
removes the keystore in an `always()` cleanup step. Secrets must not be passed
as command-line arguments, printed, persisted in artifacts, or committed.

The Google Play service account must have only the Play Console permissions
needed to manage releases and store presence for this application.

## Failure behavior and concurrency

Only one run for a given Git ref may execute at a time. A failed quality gate,
tag/version mismatch, missing secret, invalid signing key, missing metadata, or
Play API rejection stops publication and leaves the Internal track unchanged
unless Google Play already accepted the transaction.

GitHub Actions retains logs and the signed artifact according to the workflow's
explicit retention setting. Fastlane output must avoid secret values and give
an actionable error for configuration failures.

Retries use the same tag and version only when Play did not accept the version
code. If Play accepted it, any replacement build requires a higher
`versionCode`; an already-published version code cannot be overwritten.

## Verification

Repository-level verification covers:

- Ruby syntax for the `Fastfile`.
- Fastlane lane discovery and configuration validation without contacting Play.
- YAML parsing and structural assertions for tag conditions, job dependencies,
  permissions, environment, secret wiring, cleanup, and artifact retention.
- Existing Gradle unit tests and lint remain the CI quality gate.
- A local signed `bundleRelease` is run only when valid local signing
  credentials are available.

End-to-end proof requires pushing a new matching version tag after the GitHub
Environment, repository secrets, Play service account, Play App Signing, and
first Play Console application release are configured. Success means the exact
signed AAB appears as a completed release on Google Play Internal testing and
the GitHub run archives the matching bundle, mapping, and checksum.
