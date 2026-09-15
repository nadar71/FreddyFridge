# Privacy and Data Safety Checklist

Use this inventory to keep the production behavior, public privacy policy, and
Google Play Data safety form aligned.

| Data | Purpose and handling | Backup | Play declaration check |
| --- | --- | --- | --- |
| Food names, expiry dates, open/consumed state | Stored locally in Room for the core fridge list | Yes | User-generated app data; not shared by the app |
| Reminder preferences | Stored locally to schedule notifications | Yes | App functionality; not shared by the app |
| Notification content | Generated locally from food data | No separate storage | App functionality |
| Microphone audio | Sent to Android's speech-recognition service only after a user taps speech input; the app does not store audio | No | Disclose voice/audio processing according to the selected recognizer and Play guidance |
| Advertising ID and consent choices | Processed by Google Mobile Ads/UMP when consent permits | No app backup | Declare advertising, sharing, and SDK collection according to the current Google Mobile Ads disclosure |
| Crash and diagnostic data | Release builds use Firebase Crashlytics to collect crashes, ANRs, stack traces, relevant application/process state, app version, device/OS metadata, session data, Crashlytics installation UUIDs, and Firebase installation IDs for stability monitoring and debugging | Remote Firebase processing; not Android backup | Review Play **App info and performance** (crash logs and diagnostics), **Device or other IDs**, collection purpose, sharing, encryption, retention, and deletion answers against the current Firebase disclosure |

## Runtime checks

- Notification permission is requested in context and denial does not block food management.
- Microphone permission is requested only when speech input is selected; manual text input remains available.
- Mobile Ads starts only after UMP reports that ads may be requested.
- UMP-managed consent and advertising state are excluded by the backup allowlist.
- Crashlytics automatic collection is disabled for debug/testing builds and
  enabled for release builds.

## Crashlytics scope and disclosure review

This integration adds `firebase-crashlytics` only. It does not add Firebase
Analytics, Analytics breadcrumbs, custom user IDs, custom keys, manual
Crashlytics logs, or developer-recorded non-fatal events. Food names, expiry
dates, reminder preferences, notification content, email addresses, and other
application content are not deliberately attached to reports. Preserve that
boundary in future changes; any new Crashlytics customization requires another
privacy and Data Safety review.

Crashlytics still collects its default crash/ANR and diagnostic fields in
release builds. Firebase's current disclosures include stack traces, relevant
application state, device metadata, app identity/version, timestamps,
installation and session identifiers, and information from its transitive
Firebase Installations and Sessions SDKs. Firebase states that the data is
encrypted in transit and that crash traces and associated identifiers are kept
for 90 days before removal begins. Treat those details as externally maintained
and recheck them rather than copying old answers into Play Console.

Before publishing the first Crashlytics-enabled release, and whenever the SDK
or its use changes:

- compare the shipped dependency graph and code usage with Firebase's current
  [Google Play data-disclosure
  guidance](https://firebase.google.com/docs/android/play-data-disclosure) and
  [privacy information](https://firebase.google.com/support/privacy);
- update the public privacy policy to explain release crash/ANR reporting,
  purposes, identifiers/diagnostics, Google/Firebase processing, retention,
  user choices, and applicable deletion/contact route;
- update Google Play Data safety answers for collected data types, purposes,
  sharing, encryption in transit, and deletion according to the app's actual
  configuration and current Play definitions; and
- verify Firebase Analytics remains absent/disabled and that no application
  content or custom user identifier has been added to Crashlytics.

## External release blockers

- Host the public privacy policy over HTTPS and use that URL in Google Play.
- Enable HTTPS on `www.indie-walkabout.eu` before changing the in-app credits link; the HTTPS endpoint was not reachable during the 2026-08-27 audit.
- Publish `store-assets/google-play/app-ads.txt` at `https://www.indie-walkabout.eu/app-ads.txt` and verify it in AdMob.
- Complete the Crashlytics privacy-policy and Data Safety review above before
  the first tagged release; do not rely on this inventory as legal advice or as
  a substitute for the current Play questionnaire.
- Recheck the Data safety and Ads declarations whenever an SDK or data flow changes.
