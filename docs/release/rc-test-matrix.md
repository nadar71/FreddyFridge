# Release Candidate Qualification Matrix

Candidate: `2.0.2 (12)`  
Package: `eu.indiewalkabout.fridgemanager`  
Minimum / target API: `26 / 36`

This document is a release gate, not a declaration that testing happened. Use
only `PASS`, `FAIL`, `BLOCKED`, or `NOT RUN`. Every `PASS` must link or point to
evidence such as a CI run, Play report, screenshot, log, or test notes.

## Automated baseline

| Check | Command / environment | Status | Evidence |
| --- | --- | --- | --- |
| Unit regression suite | `./gradlew testDebugUnitTest` | PASS | Local, 2026-08-27 |
| Debug lint and assembly | `./gradlew lintDebug assembleDebug` | PASS | Local, 2026-08-27 |
| Instrumentation | API 36 emulator | PASS | `connectedDebugAndroidTest`, 3 tests, 2026-08-27 |
| Instrumentation | API 26 emulator | NOT RUN | Required Phase 4 CI job |
| Minified bundle | Ephemeral CI signing key | PASS | `bundleRelease`, 2026-08-27 |
| Production-signed bundle | Protected upload key | NOT RUN | Required before internal testing |

Automated success is necessary but does not replace the journeys below.

## Device and compatibility matrix

| Environment | Required coverage | Status | Evidence / owner |
| --- | --- | --- | --- |
| API 26 small phone | Cold start, CRUD, permissions, reminders | NOT RUN | CI plus manual smoke |
| API 31 phone | Splash, notifications, backup/restore | NOT RUN | Manual/emulator |
| API 33 phone | Notification grant and denial | NOT RUN | Manual/emulator |
| API 35 phone | Upgrade and daily reminder schedule | NOT RUN | Manual/emulator |
| API 36 phone | Full critical path and edge-to-edge | NOT RUN | Automated startup/settings passed; manual path remains |
| Large phone or tablet | Layout, scrolling, dialogs, keyboard | NOT RUN | Manual/emulator |
| Non-Pixel OEM | Alarm delivery and background restrictions | NOT RUN | Physical device |

## Critical journeys

Run each journey in English and Italian where text or layout is involved.

| Journey | Acceptance criteria | Status |
| --- | --- | --- |
| Fresh install | System splash and branded frame complete; home opens without crash | NOT RUN |
| Upgrade from Play `2.0.2 (12)` | Existing food and preferences remain readable | BLOCKED: candidate version must be incremented |
| Add food manually | Saved food appears immediately and survives process restart | NOT RUN |
| Add food with speech | Permission is just in time; denial leaves manual input usable | NOT RUN |
| Edit and delete food | Lists update once with no stale or duplicated rows | NOT RUN |
| Consume/open food | Correct state and date appear in the expected tab | NOT RUN |
| Tab and secondary navigation | Four tabs, settings, credits, and Back preserve expected state | NOT RUN |
| Notification deep link | Tap opens the intended destination exactly once | NOT RUN |
| Notification denial | Core food management remains usable | NOT RUN |
| Reminder schedule | Preference changes reschedule; reboot/time-zone/DST remain correct | NOT RUN |
| Consent: EEA | UMP appears when required; no ad request precedes eligibility | NOT RUN |
| Consent: non-EEA | Startup is not trapped; eligible ads can load | NOT RUN |
| Consent: offline/error | App opens and no premature ad request occurs | NOT RUN |
| Privacy options | Settings entry appears only when UMP requires it; automated UI coverage already passes | NOT RUN |
| Backup and restore | Food and app preferences restore; SDK consent state does not | NOT RUN |
| Offline mode | Local CRUD, navigation, and reminders remain usable | NOT RUN |
| Low storage/process death | No corrupt database or unrecoverable startup loop | NOT RUN |

## Accessibility and presentation

| Check | Acceptance criteria | Status |
| --- | --- | --- |
| TalkBack | Logical order, meaningful labels, no unreachable controls | NOT RUN |
| Font scale 200% | Essential content remains readable and scrollable | NOT RUN |
| Display scaling | No clipped dialogs, controls, or navigation | NOT RUN |
| Touch targets | Interactive targets meet Android guidance | NOT RUN |
| Contrast | Text/icons remain legible in all app surfaces | NOT RUN |
| EN/IT localization | No truncation, missing translation, or mixed locale | NOT RUN |
| Store consistency | Icon, screenshots, descriptions, notes, and runtime UI agree | NOT RUN |

## Performance and Play checks

| Check | Release threshold | Status |
| --- | --- | --- |
| Cold start | No visible stall after branded frame; no startup ANR | NOT RUN |
| Main interactions | CRUD and tab changes respond without sustained jank | NOT RUN |
| Play pre-launch report | No crash, ANR, security, or severe accessibility blocker | NOT RUN |
| Android vitals baseline | Record crash-free users and ANR rate before rollout | NOT RUN |

Add a Baseline Profile module only if measured startup or interaction results
show a material problem. Do not add it solely to complete a checklist.

## Exit criteria

- No `FAIL` or `BLOCKED` item remains.
- All critical journeys are `PASS` on API 26 and API 36.
- Upgrade testing uses a version code greater than the currently published app.
- Play pre-launch report has no release-blocking crash, ANR, security, or accessibility issue.
- The tested AAB checksum exactly matches the artifact selected for rollout.
