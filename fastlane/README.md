# Google Play Internal, Firebase App Distribution, and Crashlytics release operations

The `android release_internal` lane validates a version tag, synchronizes the
English and Italian Play metadata from `store-assets/google-play`, builds the
signed release AAB, and uploads it to Google Play Internal testing with release
status `completed`. GitHub Actions is the supported release entry point. The
lane does not upload to Closed testing or Production; all promotion and staged
Production rollout decisions remain manual in Play Console.

After the Play Internal job succeeds, a separate GitHub Actions job downloads
the exact retained AAB and distributes it to Firebase App Distribution group
`owner-testers`. Firebase sends first-time invitations and new-release
notifications to the members of that group. Keeping this as a dependent job
allows a Firebase-only failure to be retried without attempting to upload the
same version code to Google Play again.

The same protected build enables Firebase Crashlytics R8 mapping upload so
release crashes can be deobfuscated. It does not enable Firebase Analytics.

## One-time external setup

### Firebase applications and Android API-key restrictions

Use one Firebase project whose downloaded Android configuration contains both
of this repository's application IDs:

- Release: `eu.indiewalkabout.fridgemanager`
- Debug/testing: `eu.indiewalkabout.fridgemanager.testing`

Register both Android apps, enable Crashlytics, and download a fresh combined
`google-services.json` after both clients exist. Do not enable Google Analytics
when creating or configuring the Firebase project. The app includes
`firebase-crashlytics` only; it does not include the Analytics SDK, deprecated
Firebase KTX modules, custom Crashlytics user IDs, or application-content
logging. Automatic Crashlytics collection is disabled in debug builds and
enabled in release builds.

In **Google Cloud Console > APIs & Services > Credentials**, inspect each API
key referenced by the two Android clients. Keep its **API restrictions**
limited to the Firebase APIs required by the configured Firebase products; do
not add unrelated Google APIs to a Firebase-provisioned key. If **Application
restrictions** are set to Android apps, allow every package/certificate pair
that must use the key:

- `eu.indiewalkabout.fridgemanager` with the Google Play App Signing SHA-1
  certificate used on Play-distributed builds;
- `eu.indiewalkabout.fridgemanager` with the protected upload-key SHA-1 only
  when operators deliberately side-load an upload-signed verification build;
- `eu.indiewalkabout.fridgemanager.testing` with the actual debug/test signing
  certificate SHA-1 used for controlled non-production checks.

Do not confuse the upload certificate with the Play App Signing certificate:
Play replaces the upload signature before distribution. When a shared Android
key is used by both Firebase clients, all required package/signature pairs must
be on that key. After changing restrictions, download the configuration again
and test both clients. Firebase API keys identify the project; Firebase
Security Rules, IAM, and App Check provide authorization where those products
apply. See Firebase's current [API-key
guidance](https://firebase.google.com/docs/projects/api-keys) before changing an
automatically created allowlist.

For local configuration checks only, place the combined file at
`app/google-services.json`. The repository ignores every
`google-services.json`; never force-add it. Validate the two package IDs
without printing the file:

```bash
ruby -r ./fastlane/release_support.rb -e \
  'FreddyRelease::Support.new(project_root: Dir.pwd).validate_firebase_configuration!("app/google-services.json")'
git check-ignore -v app/google-services.json
git ls-files app/google-services.json
```

The last command must print nothing.

### Google Play and service account

1. Create the Play Console application for
   `eu.indiewalkabout.fridgemanager`. Complete any required developer-account,
   app-content, store-listing, and tester setup in Play Console.
2. Complete the first release and Play App Signing enrollment in Play Console
   if the Developer Publishing API cannot do so. Confirm that Play App Signing
   recognizes the certificate of the protected upload key before using CI.
3. In a dedicated Google Cloud project, enable the Google Play Developer API,
   create a service account, and create a JSON key for it.
4. In Play Console **Users and permissions**, invite the service-account email.
   Limit access to this application and grant only **Release apps to testing
   tracks** and **Manage store presence**. The first permission is required for
   the Internal release; the second is required because the lane also uploads
   listing text and images. Do not grant account-wide access, Admin, financial,
   order, or Production-release permissions.

See the official Google documentation for [Developer API service-account
setup](https://developers.google.com/android-publisher/getting_started) and
[Play Console permissions](https://support.google.com/googleplay/android-developer/answer/9844686).

### Firebase App Distribution

In Firebase project `e-rampart-226407`, enable App Distribution for the
FreddyFridge Android app and link that app to its Google Play application so
Firebase can process the Play AAB. The release app has these fixed identifiers:

- Package: `eu.indiewalkabout.fridgemanager`
- Firebase app ID: `1:632111455840:android:76571b3604a6c14b3aed05`

Do not use `1:632111455840:android:05746e0f2eb20e1b3aed05`; that app ID belongs
to EarthquakeWatchdog in the shared Firebase project.

Create a dedicated service account named
`freddyfridge-app-distribution@e-rampart-226407.iam.gserviceaccount.com` and
grant it the **Firebase App Distribution Admin** role. Do not substitute the
similarly named **Firebase App Distribution Admin SDK Service Agent** role.
Create the tester group with alias `owner-testers` and manage its recipients in
Firebase Console under **App Distribution > Testers and Groups**.

### Protected credentials and GitHub Environment

Keep the upload keystore and both passwords in the approved credential store;
never add them or the service-account JSON to the repository. Encode the binary
keystore as a single portable base64 line:

```bash
base64 < /absolute/path/to/freddy-upload.jks | tr -d '\n'
```

Encode the validated combined Firebase configuration the same way, without
printing either encoded value:

```bash
base64 < /absolute/path/to/google-services.json | tr -d '\n'
```

In the GitHub repository, open **Settings > Environments** and rename the
existing `google-play` environment, or create its replacement, with the exact
name `production-release`. Leave **Required reviewers** unset so an authorized
new tag remains automatic. Add these seven Environment secrets:

| Secret | Value |
| --- | --- |
| `FREDDY_UPLOAD_KEYSTORE_BASE64` | Single-line base64 of the protected upload keystore |
| `FREDDY_UPLOAD_STORE_PASSWORD` | Upload-keystore password |
| `FREDDY_UPLOAD_KEY_ALIAS` | Upload-key alias |
| `FREDDY_UPLOAD_KEY_PASSWORD` | Upload-key password |
| `GOOGLE_PLAY_JSON_KEY_DATA` | Entire service-account JSON document |
| `GOOGLE_SERVICES_JSON_BASE64` | Single-line base64 of the combined Firebase Android configuration |
| `FIREBASE_APP_DISTRIBUTION_CREDENTIALS_BASE64` | Single-line base64 of the dedicated Firebase App Distribution service-account JSON |

Add these Environment variables:

| Variable | Value |
| --- | --- |
| `FIREBASE_APP_ID` | `1:632111455840:android:76571b3604a6c14b3aed05` |
| `FIREBASE_APP_DISTRIBUTION_GROUPS` | `owner-testers` |

For compatibility with the environment's earlier configuration, the workflow
also accepts `FIREBASE_TESTER_GROUPS` when
`FIREBASE_APP_DISTRIBUTION_GROUPS` is not set. The preferred name remains
`FIREBASE_APP_DISTRIBUTION_GROUPS`.

The Play-publishing job's credential gate receives only booleans indicating
whether its six secrets are present. Later steps receive only the values they
need. It
decodes the keystore under the runner's temporary directory and the Firebase
configuration at `app/google-services.json`, sets both files to mode `0600`,
and validates both package IDs before Fastlane. An `always()` cleanup removes
both decoded files even when validation, build, publication, or evidence
collection fails. Neither file is included in workflow artifacts. Rotate a key
in its source system and replace the corresponding Environment secret; do not
commit a credential or paste a value into a workflow, log, issue, or release
record.

The dependent Firebase job separately verifies its secret and the two exact
Environment-variable values. It decodes the Firebase service-account JSON to
the runner's temporary directory with mode `0600`, validates its type, project,
and service-account email without printing the document, and removes it under
`always()`. The Firebase credential is never uploaded as an artifact.

### Protected release tags

An active GitHub tag ruleset for release tags is a prerequisite for automatic
publishing. In **Settings > Rules > Rulesets**, create a tag ruleset with these
settings before pushing the first candidate:

1. Name it `Protected v release tags`, set enforcement to **Active**, and target
   tags matching the `v*` pattern.
2. Enable **Restrict creations**, **Restrict updates**, and **Restrict
   deletions**.
3. Limit ruleset bypass to the designated release maintainers or release
   automation that must create approved tags. Do not grant a broad organization
   role or every repository administrator bypass access, and never use bypass
   access to move or delete an existing release tag.

The creation restriction ensures that only the designated release actor can
start a candidate. The update and deletion restrictions keep an existing tag
stable. The workflow independently rejects tag-update, tag-deletion, and
force-push events so that a ruleset misconfiguration cannot turn one of those
events into an automatic Play publication.

## Prepare and publish an Internal candidate

1. In `app/build.gradle.kts`, set `versionName` to the intended public version
   and increment `versionCode` above every version code already accepted by
   Play. An accepted Play version code can never be reused, including after a
   halted or failed rollout.
2. Update both localized release-note files:
   `store-assets/google-play/en-US/release-notes.txt` and
   `store-assets/google-play/it-IT/release-notes.txt`. Update localized listing
   text, images, and screenshots under the same locale directories when the
   release changes them.
3. Run the release checks from the repository root:

   ```bash
   ruby fastlane/test/release_support_test.rb
   ruby fastlane/test/crashlytics_configuration_test.rb
   ruby fastlane/test/android_ci_workflow_test.rb
   ruby -c fastlane/release_support.rb
   ruby -c fastlane/Fastfile
   BUNDLE_GEMFILE=fastlane/Gemfile bundle check
   BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane lanes
   ./gradlew testDebugUnitTest lintDebug assembleDebug
   git diff --check
   ```

4. Commit the reviewed candidate. Create an annotated tag whose name exactly
   matches `v<versionName>`, and push it:

   ```bash
   git tag -a v2.1.0 -m "Release 2.1.0"
   git push origin v2.1.0
   ```

   Replace `2.1.0` with the value in `app/build.gradle.kts`. Do not move or
   reuse a published release tag.

For a push event, the `Android CI` workflow publishes only when the matching
`v*` tag was newly created and the event is neither a deletion nor a forced
update. It runs unit tests, lint, and the debug build, plus instrumentation on
API 26 and API 36. The `publish-internal` job starts only after both quality
gates succeed. It revalidates that the tag is exactly `v<versionName>`,
synchronizes the checked-in metadata, validates the combined Firebase
configuration, builds with the protected upload key, uploads the release R8
mapping to Crashlytics, and uploads the AAB only to the Internal track. A
mapping-upload failure fails the build before Play publication. The ordinary
disposable `release-bundle` job is skipped for tag refs. Moving,
force-updating, or deleting an existing tag cannot enter the publishing job.

After `Publish to Google Play Internal` succeeds and uploads the evidence
artifact, `Distribute with Firebase App Distribution` downloads that artifact,
verifies the AAB, and runs `android distribute_firebase`. The lane uploads the
same AAB with the English release notes to `owner-testers`; it neither rebuilds
the app nor changes any Play track. Firebase then emails the configured group.

`crashlyticsMappingUploadEnabled` defaults to `false`. Ordinary local Gradle
builds, the `quality` job, and the disposable `release-bundle` validation job
therefore do not upload mappings. The Fastlane `android build_release` lane
explicitly sets it to `true` and is reserved for the protected
`production-release` flow; do not run that lane as a local smoke test.

A manual `workflow_dispatch` run can also publish when it is explicitly
dispatched against a matching `v<versionName>` tag ref. Dispatching against a
branch or a nonmatching tag cannot enter the publish job. Use this only for an
operator-controlled retry of an existing candidate, not as a substitute for
creating and pushing the release tag.

## Monitor and retain evidence

1. Follow the `Android CI` run associated with the tag. All `quality` and
   `instrumentation` matrix jobs must be green before `Publish to Google Play
   Internal` runs.
2. In Play Console **Testing > Internal testing**, confirm that the new version
   code is present, release status is completed, and the intended tester group
   can install it from Play.
3. In Firebase Console **App Distribution > FreddyFridge > Releases**, confirm
   the same version name and code is assigned to `owner-testers`. Check the
   tester status for invitation acceptance and download without copying tester
   addresses into public logs or issues.
4. Download the GitHub Actions artifact named
   `google-play-internal-<tag>`. It is retained for 30 days and contains the
   exact AAB, `mapping.txt`, and `app-release.aab.sha256`. Archive the artifact
   with the release record and verify the AAB against the archived checksum
   (for example, `shasum -a 256 <downloaded-aab>` and compare the digest).
5. Record the tag, commit, workflow URL, Play release/version code, checksum,
   and Internal tester sign-off in `docs/release/production-checklist.md`.
6. On the first tagged run after enabling Crashlytics, retain the successful
   `uploadCrashlyticsMappingFileRelease` task evidence from the build log. In
   Firebase Console, select the release Android app
   `eu.indiewalkabout.fridgemanager` under **DevOps & Engagement >
   Crashlytics**. For an event from the matching version code/name, confirm the
   report was received and its application frames show readable class, method,
   and line information rather than obfuscated symbols. Record a redacted
   Console screenshot or link in the release record; do not copy identifiers or
   report payloads into a public issue.

### Controlled non-production test crash

If no suitable event exists to prove receipt and symbolication, an authorized
operator may perform one controlled test after the tagged workflow has proved
the protected mapping-upload path:

1. Use an isolated, disposable checkout and a dedicated device or emulator
   containing no personal data. Start from the candidate source but use a
   clearly non-production version identity and keep the build out of every Play
   track.
2. Add a one-time, unmistakable test exception trigger only in that disposable
   checkout. Do not commit it, create a release tag from it, expose it in normal
   UI, or upload its APK/AAB to Play. Confirm the applicable package/signing
   pair is allowed by the Firebase Android API-key restriction.
3. Build a minified, Crashlytics-enabled operator artifact and intentionally
   upload only that build's matching R8 mapping to Firebase. Side-load it on the
   controlled device, trigger the exception once, restart the app so the report
   can be sent, and wait for the event in the matching Firebase Android app.
4. Confirm the stack is deobfuscated, retain redacted evidence, then remove the
   temporary trigger and all test artifacts. Verify the release candidate
   checkout never contained the trigger.

Follow Firebase's current [Android test-crash
procedure](https://firebase.google.com/docs/crashlytics/android/test-implementation)
when performing this operator step. This procedure causes a deliberate
external Crashlytics report and mapping upload; it is not part of local
implementation verification and requires explicit release-operator approval.

## Failure and retry decisions

- If a quality or instrumentation gate fails, no Play upload occurs. Fix the
  issue, increment the candidate when its contents change, create a new matching
  tag, and rerun the full qualification.
- If publishing fails because an environment secret, Play permission, or
  external setup item is missing, first check Play Console. When the candidate
  version code is absent, correct the external setup and rerun the failed jobs,
  or manually dispatch `Android CI` against the same matching tag.
- If Play Console already contains the version code, treat it as accepted even
  when a later evidence-upload step failed. Do not retry an upload with that
  code. Preserve the available Play and workflow evidence; any replacement AAB
  requires a higher `versionCode`, a new candidate commit, and a new tag.
- If only `Distribute with Firebase App Distribution` fails, rerun that failed
  job after correcting the Firebase secret, role, Play link, app ID, or group.
  Its successful `publish-internal` dependency and accepted Play version code
  must not be re-uploaded.
- To distribute an AAB retained by an earlier successful release run without
  touching Google Play, manually run `Android CI` with
  `firebase_artifact_run_id` set to that run's numeric ID and
  `firebase_release_tag` set to its immutable version tag. This skips ordinary
  CI and `publish-internal`, downloads only the named retained artifact, and
  runs the Firebase distribution job with `actions: read` permission.
- Do not delete and recreate or force-move a release tag. A changed build or
  metadata set is a new candidate and must use a higher version code.

After Internal qualification, promote the unchanged Play artifact through the
chosen testing tracks and Production manually in Play Console. Follow
`docs/release/production-checklist.md`; the automated Fastlane lane intentionally
has no Production target or staged-rollout interface.

## Local lane discovery

The checked-in bundle exposes these lanes:

- `android build_release` — build the signed release AAB; configure signing
  with the four `FREDDY_UPLOAD_*` environment variables or an ignored local
  `keystore.properties` file. This lane enables Crashlytics mapping upload and
  is reserved for the protected release flow.
- `android validate_release` — check the exact tag, five Fastlane credentials,
  and the combined Firebase configuration. The sixth GitHub Environment secret
  is decoded by the workflow before this lane runs.
- `android sync_store_assets` — generate Fastlane metadata from
  `store-assets/google-play`.
- `android release_internal` — validate, sync, build, and upload to Internal.
- `android distribute_firebase` — upload an existing AAB to the configured
  Firebase App Distribution group using dedicated service-account credentials.

Local lane discovery does not require release credentials:

```bash
BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane lanes
```

Do not run `release_internal` from a workstation as a smoke test. The first
end-to-end verification is a matching version-tag push after every external
setup item above is complete.
