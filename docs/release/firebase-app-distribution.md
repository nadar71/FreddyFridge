# Firebase App Distribution release specification

FreddyFridge release tags continue to publish the signed Android App Bundle to
Google Play Internal testing. After that publication succeeds, the same AAB is
distributed through Firebase App Distribution so Firebase can notify the
existing tester group automatically.

## Approved configuration

- GitHub environment: `production-release`
- Firebase project: `e-rampart-226407`
- Firebase Android app ID: `1:632111455840:android:76571b3604a6c14b3aed05`
- Firebase tester group alias: `owner-testers`
- GitHub secret: `FIREBASE_APP_DISTRIBUTION_CREDENTIALS_BASE64`
- GitHub variables: `FIREBASE_APP_ID` and `FIREBASE_APP_DISTRIBUTION_GROUPS`
- Artifact type: AAB

## Safety and retry requirements

- Google Play publishing and Firebase distribution are separate jobs so a
  Firebase-only failure can be retried without uploading the version code to
  Google Play again.
- Firebase distribution runs only after the Google Play Internal job succeeds.
- The Firebase service-account JSON is decoded to a temporary file with mode
  `0600`, validated without logging its contents, and deleted with `always()`.
- The Firebase job consumes the AAB artifact produced by the Google Play job;
  it does not rebuild or re-sign the application.
- No credential or `google-services.json` file is uploaded as an artifact.

## Current external state

- App Distribution is enabled for the FreddyFridge Firebase Android app.
- The Firebase App Distribution API is enabled for `e-rampart-226407`.
- The FreddyFridge Firebase app is linked to its Google Play application for
  AAB processing.
- The dedicated service account has `Firebase App Distribution Admin`; the
  incorrect `Firebase App Distribution Admin SDK Service Agent` role is not
  used.
- Group `owner-testers` is the approved notification and download audience.
