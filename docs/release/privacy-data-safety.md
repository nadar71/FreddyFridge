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
| Crash or diagnostic data | No first-party crash SDK is configured | No | Update if an observability SDK is added |

## Runtime checks

- Notification permission is requested in context and denial does not block food management.
- Microphone permission is requested only when speech input is selected; manual text input remains available.
- Mobile Ads starts only after UMP reports that ads may be requested.
- UMP-managed consent and advertising state are excluded by the backup allowlist.

## External release blockers

- Host the public privacy policy over HTTPS and use that URL in Google Play.
- Enable HTTPS on `www.indie-walkabout.eu` before changing the in-app credits link; the HTTPS endpoint was not reachable during the 2026-08-27 audit.
- Publish `store-assets/google-play/app-ads.txt` at `https://www.indie-walkabout.eu/app-ads.txt` and verify it in AdMob.
- Recheck the Data safety and Ads declarations whenever an SDK or data flow changes.
