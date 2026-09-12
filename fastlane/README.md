# Fastlane release setup

This project uses Fastlane for Google Play release automation.

- `bundleRelease` output is built from `app/build.gradle.kts` release variant.
- Store metadata and assets are synchronized from `store-assets/google-play`.
- Store assets are uploaded for locales en-US and it-IT.

## Required env vars

Set one of these in your shell before running release lanes:

- `GOOGLE_PLAY_JSON_KEY` (path to a Google Play service account JSON key file), or
- `GOOGLE_PLAY_JSON_KEY_DATA` (raw JSON payload for the key).

## Common commands

Build and upload to Internal testing:

```bash
bundle exec fastlane android release
```

Build only:

```bash
bundle exec fastlane android build_release
```

Resync metadata from store assets:

```bash
bundle exec fastlane android sync_store_assets
```

Upload to another track:

```bash
bundle exec fastlane android release track:production
```

Release status options: `draft`, `inProgress`, or `completed`.
