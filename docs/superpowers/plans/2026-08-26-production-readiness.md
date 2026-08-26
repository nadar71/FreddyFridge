# FreddyFridge Production Readiness Implementation Plan

> **For Codex:** Execute one phase at a time. Keep every phase independently reviewable and commit only after its gate passes.

**Goal:** Produce a signed, policy-compliant, observable, and regression-tested Android App Bundle that can be safely rolled out through Google Play.

**Architecture:** Preserve the existing single-module Clean MVVM and Navigation 3 structure. Add release hardening around the current feature boundaries rather than performing unrelated architecture rewrites. Isolate Android framework work behind testable reminder, consent, and build configuration boundaries.

**Tech Stack:** Kotlin, Jetpack Compose, Navigation 3, Room, Hilt, Coroutines/Flow, AlarmManager, Google Mobile Ads/UMP, Gradle Kotlin DSL, JUnit, AndroidX instrumentation/Compose testing, Google Play Console.

**Spec:** Current repository audit on 2026-08-26. Google Play requires new apps and app updates to target API 36 from 2026-08-31. Official policy: https://support.google.com/googleplay/android-developer/answer/11926878

**Global Constraints:** Keep `minSdk = 26`; preserve EN/IT behavior and existing store assets; do not commit signing keys or credentials; do not weaken reminder behavior silently; use production ad units only in release and test-device configuration only in debug; each phase must leave `develop` buildable.

---

## Phase 1: Remove Submission And Runtime Blockers

### Task 1: Target Android 16 safely

**Files:**
- Modify: `app/build.gradle.kts:16-24`
- Review: `app/src/main/AndroidManifest.xml`
- Test: `app/src/test/java/eu/indiewalkabout/fridgemanager/core/presentation/navigation/AppNavigationStateTest.kt`

- [ ] Change `targetSdk` from 35 to 36.
- [ ] Review all Android 16 behavior changes relevant to this app: edge-to-edge/insets, notifications, intents, foreground/background execution, and local-network behavior from bundled SDKs.
- [ ] Add or adjust a startup/navigation regression test before changing behavior where a testable contract exists.
- [ ] Run `./gradlew testDebugUnitTest assembleDebug`.
- [ ] Launch on an API 36 emulator and verify cold start, all four tabs, settings, credits, add/edit food, speech permission denial, and notification permission denial.
- [ ] Commit: `build: target Android 16 for Play compliance`.

**Gate:** An API 36 device completes the smoke path without crash, broken insets, or blocked navigation.

### Task 2: Make reminder delivery lifecycle-safe

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_notifications/domain/reminder/AlarmReceiver.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_notifications/domain/reminder/AlarmReminderScheduler.kt`
- Create: `app/src/test/java/eu/indiewalkabout/fridgemanager/feat_notifications/domain/reminder/ReminderScheduleCalculatorTest.kt`
- Create: `app/src/test/java/eu/indiewalkabout/fridgemanager/feat_notifications/domain/reminder/ReminderNotificationCoordinatorTest.kt`

- [ ] First add failing tests for zero notifications, normal daily slots, late-day startup, next-day rollover, and daylight-saving/time-zone boundaries.
- [ ] Extract deterministic schedule calculation from `Calendar`/`AlarmManager` calls.
- [ ] Replace unmanaged `CoroutineScope(Dispatchers.IO)` launches in `BroadcastReceiver.onReceive()` with `goAsync()` and always finish the pending result, or delegate durable work to an appropriate worker if execution can exceed receiver limits.
- [ ] Combine duplicate database reads/notification decisions where practical and ensure failures cannot leave the receiver unfinished.
- [ ] Verify alarm cancellation and rescheduling after preference changes.
- [ ] Run focused tests, then `./gradlew testDebugUnitTest assembleDebug`.
- [ ] Commit: `fix: make reminder scheduling and delivery reliable`.

**Gate:** Reminder calculations are deterministic and receiver work cannot be killed merely because `onReceive()` returned.

### Task 3: Minimize alarm permission and component exposure

**Files:**
- Modify: `app/src/main/AndroidManifest.xml:12-13,48-53`
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_notifications/util/extensions/ContextExtensions.kt`
- Test: reminder tests from Task 2

- [ ] Decide and document whether hour-wide reminder delivery truly requires exact alarms.
- [ ] Prefer inexact/window alarms and remove exact-alarm permission/request UX if product behavior permits it.
- [ ] If exact delivery is essential, retain `SCHEDULE_EXACT_ALARM`, handle grant denial gracefully, and do not request restricted `USE_EXACT_ALARM` unless the Play policy use case is demonstrably eligible.
- [ ] Set `AlarmReceiver` to `android:exported="false"`; all current calls use explicit internal intents.
- [ ] Remove the inaccurate manifest comment about exact-alarm permission history.
- [ ] Verify immutable pending intents and Android 12-16 grant/deny paths.
- [ ] Run `./gradlew testDebugUnitTest lintDebug assembleDebug` after the lint dependency fix in Phase 2 if lint is still blocked.
- [ ] Commit: `security: restrict reminder alarms and receiver exposure`.

**Gate:** The manifest requests only justified permissions and no external app can invoke the alarm receiver.

---

## Phase 2: Harden The Release Build

### Task 4: Repair dependency scopes and lint

**Files:**
- Modify: `app/build.gradle.kts:47-153`
- Modify: `gradle/libs.versions.toml`

- [ ] Move `androidx.ui.test.android` out of `implementation` and keep UI tooling debug-only.
- [ ] Resolve the Espresso 3.6.1 versus strict 3.5.0 Android-test dependency conflict.
- [ ] Confirm usage before removing duplicate Coil, Navigation 2, Retrofit/OkHttp, multidex, WorkManager, duplicate lifecycle, or preference dependencies.
- [ ] Remove only proven-unused dependencies in a separate commit to keep failures attributable.
- [ ] Run `./gradlew dependencies lintDebug testDebugUnitTest assembleDebug`.
- [ ] Commit: `build: fix test scopes and dependency consistency`.

**Gate:** Full debug lint runs successfully with no dependency-resolution blocker.

### Task 5: Configure secure release signing

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `.gitignore`
- Create: `keystore.properties.example`
- Modify: project release documentation

- [ ] Add release signing configuration sourced from environment variables or an ignored local properties file.
- [ ] Fail a release build with a clear message when signing inputs are absent; never fall back to debug signing.
- [ ] Keep the upload key and passwords outside Git and back them up securely.
- [ ] Enable Play App Signing in Play Console and distinguish upload key from app-signing key in documentation.
- [ ] Build a signed local AAB with `./gradlew bundleRelease` using non-committed credentials.
- [ ] Inspect certificate/package/version with `bundletool` or `jarsigner`.
- [ ] Commit: `build: add secure release signing configuration`.

**Gate:** A correctly signed `app-release.aab` is produced without secrets entering Git.

### Task 6: Enable and verify release optimization

**Files:**
- Modify: `app/build.gradle.kts:26-33`
- Modify: `app/proguard-rules.pro`

- [ ] Enable R8 minification and resource shrinking for release.
- [ ] Start with no blanket keep rules; add narrow rules only for failures demonstrated by the release build or smoke tests.
- [ ] Preserve source/line metadata needed for retraced crash reports.
- [ ] Run `./gradlew bundleRelease` and inspect R8 warnings, mapping output, app size, and merged manifest.
- [ ] Install the universal APK generated from the AAB and repeat the critical smoke path.
- [ ] Commit: `build: enable verified release shrinking`.

**Gate:** The minified release starts, reads the Room database, navigates, schedules reminders, displays consent UI, and loads eligible ads.

---

## Phase 3: Privacy, Ads, Backup, And Store Compliance

### Task 7: Harden consent and ad configuration

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_ads/util/ConsentManager.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_starting/presentation/ui/intromain/MainActivity.kt`
- Modify: ad resource/configuration files under `app/src/main/res`
- Extend: `app/src/test/java/eu/indiewalkabout/fridgemanager/feat_ads/util/RequestConfigurationUtilsTest.kt`
- Create: consent state tests

- [ ] Add tests for consent success, not-required, form error, retry, privacy-options availability, and duplicate initialization.
- [ ] Replace the unsafe `context as Activity` path with an explicit Activity contract.
- [ ] Ensure Mobile Ads initializes only when UMP says ads may be requested and only once.
- [ ] Keep test device IDs/debug geography in debug builds only; verify release uses production application and unit IDs.
- [ ] Expose a privacy-options entry in settings whenever UMP requires it rather than treating consent reset as the normal user flow.
- [ ] Configure and verify `app-ads.txt` for the production package/domain.
- [ ] Commit: `fix: harden consent and production ad startup`.

**Gate:** EEA consent, non-EEA, offline/error, and returning-user paths never request ads prematurely or trap startup.

### Task 8: Define data handling and backup policy

**Files:**
- Modify: `app/src/main/AndroidManifest.xml:25-35`
- Create: `app/src/main/res/xml/backup_rules.xml`
- Create: `app/src/main/res/xml/data_extraction_rules.xml`
- Update: privacy policy and Play Data safety working document

- [ ] Inventory Room food data, preferences, ad identifiers, diagnostics, microphone input, and notification data as collected, shared, transient, or local-only.
- [ ] Decide whether user food data/preferences should restore to a new device; replace `fullBackupContent="true"` with explicit backup/data-extraction rules or disable backup.
- [ ] Ensure no signing data, ad configuration secrets, or transient consent state is backed up unintentionally.
- [ ] Replace the remaining public website URL with HTTPS if the endpoint supports it.
- [ ] Verify microphone and notification permissions are requested just in time and denial leaves usable fallback UI.
- [ ] Align the public privacy policy, Data safety form, Ads declaration, and in-app behavior.
- [ ] Commit: `privacy: define backup and data handling policy`.

**Gate:** Manifest, runtime behavior, privacy policy, and Play declarations describe the same data practices.

---

## Phase 4: Regression Coverage And CI

### Task 9: Add critical instrumentation journeys

**Files:**
- Replace: `app/src/androidTest/java/eu/indiewalkabout/fridgemanager/ExampleInstrumentedTest.kt`
- Create: focused instrumentation/Compose tests under `app/src/androidTest/java/...`

- [ ] Add a cold-start test that proves the system splash, branded frame, and Navigation 3 root do not crash.
- [ ] Add CRUD coverage for adding, editing, deleting, filtering, and reopening food after activity recreation.
- [ ] Add tab-switching and settings/credits back-navigation coverage.
- [ ] Add notification-intent navigation coverage and notification permission denial coverage.
- [ ] Add Room migration tests for every schema version that can exist in production; export schemas to version control.
- [ ] Avoid timing-sensitive assertions against splash animation internals; assert stable outcomes.
- [ ] Run unit and instrumentation suites on API 26 and API 36.
- [ ] Commit: `test: cover production-critical user journeys`.

**Gate:** Critical local-data and navigation journeys fail automatically when regressed.

### Task 10: Add continuous release checks

**Files:**
- Create: `.github/workflows/android-ci.yml` (or equivalent CI for the repository host)
- Modify: `gradle.properties` only for non-secret deterministic settings

- [ ] On every pull request run Gradle wrapper validation, unit tests, lint, and debug assembly.
- [ ] On protected release refs run instrumentation tests and unsigned release bundle validation; keep signing in a protected release job only.
- [ ] Cache Gradle safely and cancel superseded branch runs.
- [ ] Upload lint/test reports and AAB artifacts with bounded retention.
- [ ] Add dependency/security update automation without auto-merging production dependencies.
- [ ] Protect `develop` so required checks must pass before merge.
- [ ] Commit: `ci: enforce Android quality and release gates`.

**Gate:** A clean clone can reproduce all non-secret checks and a protected job can produce the release artifact.

---

## Phase 5: Release Candidate Qualification

### Task 11: Validate compatibility, accessibility, and performance

**Files:**
- Create/update: `docs/release/rc-test-matrix.md`
- Optionally add: Macrobenchmark/Baseline Profile module in a dedicated follow-up commit

- [ ] Test API 26, 31, 33, 35, and 36; include a small phone, large phone/tablet layout check, and at least one non-Pixel OEM.
- [ ] Test EN and IT, large fonts, display scaling, TalkBack labels/order, contrast, and touch targets.
- [ ] Test fresh install, update from the current Play version, process death, reboot/time-zone/DST changes, offline mode, low storage, and denied permissions.
- [ ] Run Play pre-launch report and resolve crashes, ANRs, accessibility blockers, and severe security findings.
- [ ] Measure cold startup and main interactions; add Baseline Profiles only if measurements show worthwhile improvement.
- [ ] Verify store screenshots, icon, feature graphic, descriptions, version code/name, and content rating against the actual RC.
- [ ] Commit documentation/results: `docs: record release candidate qualification`.

**Gate:** No known P0/P1 defects, no startup/CRUD/reminder failures, and no unresolved Play pre-launch blocker.

### Task 12: Perform staged production rollout

**Files:**
- Create/update: `docs/release/production-checklist.md`

- [ ] Publish the exact RC AAB to internal testing and validate install/update from Play.
- [ ] Complete Data safety, Ads, Content rating, Target audience, privacy policy, and exact-alarm declarations as applicable.
- [ ] Preserve `mapping.txt` and native/debug symbols for every release.
- [ ] Define rollout stop thresholds for crash-free users, ANR rate, reminder regressions, and ad/consent failures.
- [ ] Roll out progressively, for example 5%, 20%, 50%, then 100%, with an observation window at each stage.
- [ ] Prepare a rollback/hotfix branch procedure before starting production rollout.
- [ ] Tag the released commit and record Play version code, artifact checksum, and release notes.
- [ ] Commit: `docs: finalize production rollout runbook`.

**Gate:** Production reaches 100% only while health metrics remain within the documented thresholds.

---

## Definition Of Production Ready

- [ ] `targetSdk = 36`, and API 36 behavior has been tested.
- [ ] Unit tests, instrumentation tests, lint, debug assembly, and signed/minified release bundle all pass.
- [ ] Reminder work is lifecycle-safe and exact-alarm use is policy-justified or removed.
- [ ] Exported components and requested permissions are minimized.
- [ ] UMP consent, production ads, privacy options, and `app-ads.txt` are verified.
- [ ] Backup policy, privacy policy, and Play Data safety declarations match runtime behavior.
- [ ] Upgrade testing preserves existing user data.
- [ ] Play pre-launch report has no release blocker.
- [ ] Monitoring, retrace artifacts, staged rollout thresholds, and rollback procedure are ready.
