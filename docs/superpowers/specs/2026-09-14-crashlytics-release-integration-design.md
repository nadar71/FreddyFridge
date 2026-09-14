# Crashlytics Release Integration Design

## Goal

Add Firebase Crashlytics crash and ANR reporting to FreddyFridge without
enabling Firebase Analytics, while preserving local debug builds and the
tag-triggered Fastlane upload to Google Play Internal testing.

## Firebase applications and configuration

The Firebase project contains Android clients for both application IDs used by
this repository:

- Release: `eu.indiewalkabout.fridgemanager`
- Debug: `eu.indiewalkabout.fridgemanager.testing`

`app/google-services.json` contains both clients and is used for local
verification, but it remains outside Git. `.gitignore` will explicitly ignore
`**/google-services.json`, matching the established EarthquakeWatchdog release
pattern.

GitHub Actions receives the file through a new `GOOGLE_SERVICES_JSON_BASE64`
Environment secret. The publishing job decodes it to
`app/google-services.json`, validates that both exact package IDs exist, limits
the file to mode `0600`, and removes it in an `always()` cleanup step. The JSON
contents, Firebase identifiers, and API keys must never appear in logs or
artifacts.

## Gradle integration

The version catalog will define:

- Firebase Android BoM `34.19.0`
- Google Services Gradle plugin `4.5.0`
- Firebase Crashlytics Gradle plugin `3.0.8`
- `com.google.firebase:firebase-crashlytics` with its version supplied by the
  BoM

The root build declares both plugins with `apply false`. The app module applies
Google Services and Crashlytics and adds the Firebase BoM plus the main
Crashlytics module. Firebase Analytics and all deprecated Firebase KTX modules
remain absent.

The release build uploads its R8 mapping through the Crashlytics Gradle plugin.
The Fastlane release build explicitly sets
`-PcrashlyticsMappingUploadEnabled=true`; ordinary local and CI validation
builds default mapping upload to `false`, preventing accidental external
uploads.

## Collection policy

Crashlytics automatic collection is disabled for the debug build and enabled
for release through the manifest placeholder
`CRASHLYTICS_COLLECTION_ENABLED`. The application manifest maps that value to
`firebase_crashlytics_collection_enabled`.

The integration does not add custom user identifiers, email addresses, food
names, expiry dates, preference values, or other application content to crash
reports. It does not add manual non-fatal logging in this change. Crashlytics
collects the default crash, ANR, stack, process, device, application, and
diagnostic information described by Firebase.

## GitHub Environment and release workflow

The protected GitHub Environment is renamed from `google-play` to
`production-release`. The name represents the protected production signing and
Firebase identities, although the current automated publishing target remains
Google Play Internal testing only.

`production-release` has no required reviewer so newly created matching tags
remain automatic. It contains the existing five Play/signing secrets plus:

- `GOOGLE_SERVICES_JSON_BASE64`: single-line base64 encoding of the validated
  combined Firebase Android configuration.

The publishing workflow updates its credential-presence gate to require all
six secrets. Firebase provisioning happens after pinned tool setup and before
Fastlane. Keystore and Firebase configuration cleanup both use `always()`.
The signed AAB, R8 mapping, and checksum remain the only release artifacts.

The `v*` tag ruleset, new/non-forced tag checks, fixed Internal track, and
manual Production promotion remain unchanged.

## Fastlane release behavior

The `build_release` lane passes the explicit Crashlytics mapping-upload Gradle
property. `validate_release` requires the Firebase configuration secret in
addition to the existing signing and Play credentials. The lane still:

1. Validates tag, credentials, versions, and metadata.
2. Synchronizes English and Italian Play listing media.
3. Builds the minified production-signed AAB and uploads its R8 mapping to
   Firebase.
4. Uploads the AAB and media to Google Play Internal with status `completed`.

A Firebase mapping-upload failure fails the release build rather than silently
publishing an artifact whose crashes cannot be deobfuscated.

## Privacy and operator documentation

`docs/release/privacy-data-safety.md` will replace the statement that no crash
SDK exists with Crashlytics diagnostic-data behavior. Operators must reconcile
the public privacy policy and Google Play Data safety form before publication,
including crash logs/diagnostics, device or other identifiers when applicable,
collection purpose, encryption in transit, and deletion behavior according to
the current Firebase disclosure.

`fastlane/README.md` will document the renamed Environment, sixth secret,
portable encoding command, Firebase client requirements, local configuration,
mapping upload, Console verification, and the fact that Analytics is not
enabled.

## Verification

Tests will verify:

- Both Gradle plugins, the BoM, and Crashlytics-only dependency are present at
  their exact versions while Analytics/KTX dependencies are absent.
- Debug and release collection placeholders are respectively `false` and
  `true`.
- Mapping upload defaults off and the protected Fastlane release opts in.
- `google-services.json` stays ignored and is absent from tracked files.
- CI uses `production-release`, requires the sixth secret, validates both
  package IDs without logging the JSON, and always removes the file.
- The existing release-support and workflow tests continue to pass.
- Debug unit tests, lint, and assembly pass with the validated local
  configuration.
- A release Gradle dry run reaches the Crashlytics mapping-upload task when
  disposable signing credentials are supplied.

No test crash will be shipped. End-to-end verification requires the first
tagged GitHub run and confirmation in Firebase Console that the release build
appears and its obfuscated stack traces can use the uploaded mapping file.
