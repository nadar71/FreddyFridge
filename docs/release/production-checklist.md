# Production Release and Rollout Runbook

No checkbox in this document may be completed from an unverified assumption.
Attach the Play Console URL, CI run, checksum, report, or operator initials where
the action occurred.

## 1. Candidate identity

- [ ] Increment `versionCode` above the latest Play artifact (`12` at the time of this audit).
- [ ] Set the intended user-facing `versionName` and update EN/IT release notes.
- [ ] Build the candidate from a reviewed commit on `develop` or a release branch.
- [ ] Run the complete `docs/release/rc-test-matrix.md`; no `FAIL`, `BLOCKED`, or `NOT RUN` release gate may remain.
- [ ] Build with the protected upload key: `./gradlew clean bundleRelease`.
- [ ] Record commit: `________________`.
- [ ] Record version code/name: `________________`.
- [ ] Record CI run: `________________`.
- [ ] Record AAB SHA-256: `shasum -a 256 app/build/outputs/bundle/release/app-release.aab`.
- [ ] Archive the exact AAB and `app/build/outputs/mapping/release/mapping.txt` together.

Never upload the ephemeral CI validation artifact to Play. It proves the release
pipeline and R8 configuration only; it is not signed by the protected upload key.

## 2. Store and policy gate

- [ ] Privacy policy is publicly reachable over HTTPS and matches `privacy-data-safety.md`.
- [ ] `app-ads.txt` is reachable at the developer website root and verified by AdMob.
- [ ] Data safety declares local food/reminder data and current Google Mobile Ads/UMP behavior accurately.
- [ ] Ads declaration identifies that the app contains ads.
- [ ] Target audience, content rating, app access, and content declarations are complete.
- [ ] Exact-alarm declaration is not required; the app uses inexact alarms.
- [ ] Store icon, feature graphic, screenshots, EN/IT descriptions, and release notes match the candidate.
- [ ] Play App Signing is enabled and the upload certificate matches the protected upload key.
- [ ] Play pre-launch report has no unresolved release-blocking crash, ANR, security, or severe accessibility issue.

## 3. Internal and closed validation

- [ ] Upload the exact candidate AAB to Internal testing.
- [ ] Install from Play on a fresh device; do not side-load this verification build.
- [ ] Upgrade from the current production version and verify food/preferences remain intact.
- [ ] Complete the API 26 and API 36 critical paths from the RC matrix.
- [ ] Verify EEA consent, non-EEA consent, privacy options, offline startup, and eligible ad loading.
- [ ] Verify notification denial, microphone denial, reminder delivery, reboot, time-zone change, and notification deep link.
- [ ] Promote the unchanged artifact to Closed testing if additional testers are required.
- [ ] Collect tester sign-off and known-issue decision: `________________`.

Any code change after upload creates a new candidate and restarts qualification.

## 4. Production rollout

Use Play staged rollout and promote the same artifact. Observation windows are
minimums; extend them when the active-user sample is too small to be meaningful.

| Stage | Minimum observation | Promotion gate |
| --- | --- | --- |
| 5% | 24 hours | No P0/P1 report; health thresholds remain green |
| 20% | 24 hours | Upgrade, reminders, consent, and ads show no regression |
| 50% | 48 hours | Stable vitals and support volume; no new severe Play report |
| 100% | Continuous | Monitor for at least seven days after completion |

Record each promotion:

| Percentage | Started at | Ended at | Vitals/support evidence | Operator |
| --- | --- | --- | --- | --- |
| 5% | | | | |
| 20% | | | | |
| 50% | | | | |
| 100% | | | | |

## 5. Stop conditions

Halt the rollout and start triage when any condition is met:

- Any reproducible data loss, startup loop, security/privacy violation, or reminder storm.
- Any new P0/P1 issue affecting the critical food-management path.
- Crash-free users below `99.5%` for the candidate when the sample is meaningful.
- User-perceived ANR rate above `0.2%` for the candidate when the sample is meaningful.
- A clear candidate-over-baseline increase in startup crashes, consent failures, notification failures, or support contacts.
- Play pre-launch or policy review reports a release blocker.

These are FreddyFridge product stop thresholds, intentionally used as an early
warning. Recheck current Play bad-behavior thresholds in Console for every release.
Do not continue merely because the platform threshold has not yet been crossed.

## 6. Halt, rollback, and hotfix

1. Halt the staged rollout in Play Console; do not increase the percentage.
2. Preserve logs, affected versions, device/API details, timestamps, and screenshots.
3. Classify severity and decide whether stopping rollout is sufficient or a hotfix is required.
4. Branch `hotfix/<issue>` from the exact production tag, not from unqualified development work.
5. Add a regression test before the fix where technically possible.
6. Increment `versionCode`, rebuild with the protected key, and repeat the RC gate for affected and critical paths.
7. Publish the hotfix through Internal testing before a new staged production rollout.
8. Record the incident and prevention action in release notes or an internal postmortem.

Google Play cannot replace an already installed version with an older version
code. Recovery requires halting the rollout and, when necessary, shipping a fixed
artifact with a higher version code.

## 7. Close release

- [ ] Confirm rollout reached 100% while all stop conditions remained clear.
- [ ] Create annotated tag `v<versionName>` on the exact released commit.
- [ ] Push the tag and retained release branch.
- [ ] Record Play release ID and publication timestamps.
- [ ] Store AAB checksum, mapping file, release notes, test evidence, and Play reports in the release record.
- [ ] Confirm production vitals after 24 hours, 72 hours, and seven days.
- [ ] Convert deferred non-blocking findings into tracked follow-up work.

Release record owner: `________________`  
Final approval: `________________`  
Release date: `________________`
