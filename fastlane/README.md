# Google Play Internal release operations

The `android release_internal` lane validates a version tag, synchronizes the
English and Italian Play metadata from `store-assets/google-play`, builds the
signed release AAB, and uploads it to Google Play Internal testing with release
status `completed`. GitHub Actions is the supported release entry point. The
lane does not upload to Closed testing or Production; all promotion and staged
Production rollout decisions remain manual in Play Console.

## One-time external setup

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

### Protected upload key and GitHub Environment

Keep the upload keystore and both passwords in the approved credential store;
never add them or the service-account JSON to the repository. Encode the binary
keystore as a single portable base64 line:

```bash
base64 < /absolute/path/to/freddy-upload.jks | tr -d '\n'
```

In the GitHub repository, open **Settings > Environments**, create an
environment named exactly `google-play`, and leave **Required reviewers**
unset. Add these five environment secrets:

| Secret | Value |
| --- | --- |
| `FREDDY_UPLOAD_KEYSTORE_BASE64` | Single-line output of the command above |
| `FREDDY_UPLOAD_STORE_PASSWORD` | Upload-keystore password |
| `FREDDY_UPLOAD_KEY_ALIAS` | Upload-key alias |
| `FREDDY_UPLOAD_KEY_PASSWORD` | Upload-key password |
| `GOOGLE_PLAY_JSON_KEY_DATA` | Entire service-account JSON document |

The workflow checks only whether all five secrets are present. It decodes the
keystore under the runner's temporary directory, restricts its permissions,
and removes it even when the publish step fails. Rotate a key in its source
system and replace the corresponding environment secret; do not commit a key
or paste a value into a workflow or issue.

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
   ruby fastlane/test/android_ci_workflow_test.rb
   ruby -c fastlane/release_support.rb
   ruby -c fastlane/Fastfile
   BUNDLE_GEMFILE=fastlane/Gemfile bundle check
   BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane lanes
   ./gradlew testDebugUnitTest lintDebug
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
synchronizes the checked-in metadata, builds with the protected upload key, and
uploads the AAB only to the Internal track. The ordinary disposable
`release-bundle` job is skipped for tag refs. Moving, force-updating, or deleting
an existing tag cannot enter the publishing job.

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
3. Download the GitHub Actions artifact named
   `google-play-internal-<tag>`. It is retained for 30 days and contains the
   exact AAB, `mapping.txt`, and `app-release.aab.sha256`. Archive the artifact
   with the release record and verify the AAB against the archived checksum
   (for example, `shasum -a 256 <downloaded-aab>` and compare the digest).
4. Record the tag, commit, workflow URL, Play release/version code, checksum,
   and Internal tester sign-off in `docs/release/production-checklist.md`.

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
  `keystore.properties` file.
- `android validate_release` — check the exact tag and all five credentials.
- `android sync_store_assets` — generate Fastlane metadata from
  `store-assets/google-play`.
- `android release_internal` — validate, sync, build, and upload to Internal.

Local lane discovery does not require release credentials:

```bash
BUNDLE_GEMFILE=fastlane/Gemfile bundle exec fastlane lanes
```

Do not run `release_internal` from a workstation as a smoke test. The first
end-to-end verification is a matching version-tag push after every external
setup item above is complete.
