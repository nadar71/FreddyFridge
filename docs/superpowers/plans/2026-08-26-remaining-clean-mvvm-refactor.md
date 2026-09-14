# Remaining Clean MVVM Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the remaining Clean Code and MVVM refactor through small, independently testable commits without changing app behavior.

**Architecture:** First standardize secondary-screen composition, then separate Compose rendering from state and side-effect orchestration, and finally tighten domain/data boundaries. Keep Android framework actions at the UI or injected gateway boundary, expose immutable state from ViewModels, and retain reactive `Flow` reads for screen data.

**Tech Stack:** Kotlin, Jetpack Compose, Navigation 3, Hilt, Coroutines and Flow, Room, JUnit, kotlinx-coroutines-test.

**Spec:** This plan refines the previously agreed remaining roadmap: secondary-screen shell alignment, screen orchestration cleanup, ViewModel responsibility review, repository/use-case cleanup, regression expansion, and final architecture review.

## Global Constraints

- Preserve current user-visible behavior, navigation destinations, splash sequence, advertisements, notification scheduling, and stored preferences.
- Keep the completed Navigation 3 implementation and `TopLevelScreenScaffold` behavior intact.
- Use lifecycle-aware Flow collection in Compose entry points.
- Keep composable content functions stateless and previewable where practical.
- Add or update regression tests before each behavioral refactor.
- Run targeted tests and `:app:compileDebugKotlin` before every commit.
- One checklist task equals one focused commit; do not combine tasks unless the preceding task is mechanically inseparable.

---

## Phase 1: Standardize Screen Structure

Outcome: settings and secondary screens share predictable layout primitives while retaining their distinct navigation behavior.

### Task 1.1: Add a secondary-screen scaffold

**Files:**
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/core/presentation/navigation/components/SecondaryScreenScaffold.kt`
- Reference: `app/src/main/java/eu/indiewalkabout/fridgemanager/core/presentation/navigation/components/TopLevelScreenScaffold.kt`

**Interface:**
- Produce `SecondaryScreenScaffold(showBottomBar: Boolean, bottomBar: @Composable () -> Unit, content: @Composable BoxScope.() -> Unit)` or the smallest equivalent API required by Settings and Credits.
- Own only the shared `Scaffold`, primary container color, padding, and `BackgroundPattern`; do not absorb screen-specific top bars or actions.

- [ ] Add the scaffold with a focused, slot-based API.
- [ ] Add Compose previews or preview callers covering content with and without a bottom bar.
- [ ] Run `GRADLE_USER_HOME=/tmp/mapopinball-gradle ./gradlew :app:compileDebugKotlin`.
- [ ] Commit as `refactor: add secondary screen scaffold`.

### Task 1.2: Move Settings onto the secondary scaffold

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/settings/SettingsScreen.kt`
- Modify if needed: `app/src/main/java/eu/indiewalkabout/fridgemanager/core/presentation/navigation/components/SecondaryScreenScaffold.kt`

- [ ] Replace Settings' duplicated `Scaffold`, `Box`, `BackgroundPattern`, and bottom-bar shell with `SecondaryScreenScaffold`.
- [ ] Preserve scrolling, dialogs, ad placement, bottom navigation appearance, and all click behavior.
- [ ] Remove unused imports and the derived `isFabVisible` state if it still has no renderer or behavior consumer.
- [ ] Compile and manually open Settings from Main, then return through bottom navigation.
- [ ] Commit as `refactor: align settings screen scaffold`.

### Task 1.3: Move Credits onto the secondary scaffold

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/credits/CreditsScreen.kt`
- Modify if needed: `app/src/main/java/eu/indiewalkabout/fridgemanager/core/presentation/navigation/components/SecondaryScreenScaffold.kt`

- [ ] Replace Credits' duplicated shell with `SecondaryScreenScaffold` while keeping its back top bar inside screen content.
- [ ] Preserve all external-link actions and the Navigation 3 back callback.
- [ ] Remove dead declarations, commented legacy UI where safe, and unused imports.
- [ ] Compile and manually verify Settings -> Credits -> Back.
- [ ] Commit as `refactor: align credits screen scaffold`.

**Phase 1 gate:** Settings and Credits render and navigate as before; `:app:compileDebugKotlin` passes and the worktree is clean after three commits.

---

## Phase 2: Separate Presentation State and Side Effects

Outcome: route composables coordinate dependencies, content composables render immutable state, and business state no longer lives directly in large screen functions.

### Task 2.1: Split Main route orchestration from rendering

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_starting/presentation/ui/intromain/MainScreen.kt`
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_starting/presentation/ui/intromain/MainScreenEffects.kt`
- Test: `app/src/test/java/eu/indiewalkabout/fridgemanager/feat_starting/presentation/ui/intromain/MainUiStateReducerTest.kt`

**Interface:**
- Keep `MainScreen(...)` as the Navigation 3/Hilt route entry point.
- Produce a stateless `MainScreenContent(uiState, selectedDestination, onNavigateToDestination, onOpenSettings, onShowOnBoarding, onAddFood)`.
- Centralize event collection and loading/result forwarding in `MainScreenEffects` without putting Android views or repositories into content.

- [ ] Extend reducer tests for insert/update loading, success, failure, and emitted UI events before moving orchestration.
- [ ] Run `:app:testDebugUnitTest --tests eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain.MainUiStateReducerTest` and confirm they pass against current behavior.
- [ ] Extract `MainScreenContent` and move effect-only `LaunchedEffect` blocks into `MainScreenEffects`.
- [ ] Replace `collectAsState()` with `collectAsStateWithLifecycle()` at the route boundary.
- [ ] Compile, run the targeted reducer test, and manually verify add/update/delete feedback on Main.
- [ ] Commit as `refactor: separate main route from content`.

### Task 2.2: Introduce testable Settings state ownership

**Files:**
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/domain/repository/SettingsRepository.kt`
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/data/repository/SettingsRepositoryImpl.kt`
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/settings/SettingsUiState.kt`
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/settings/SettingsViewModel.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/core/di/RepositoryModule.kt`
- Test: `app/src/test/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/settings/SettingsViewModelTest.kt`

**Interface:**
- `SettingsRepository` exposes current notification settings and explicit update/reset operations, hiding direct `AppPreferences` access from presentation.
- `SettingsViewModel` exposes immutable `StateFlow<SettingsUiState>` and accepts intent methods such as `updateDaysBeforeDeadline`, `updateDailyNotificationCount`, and `resetPreferences`.

- [ ] Write ViewModel tests using a fake repository for initial state, each setting update, and reset behavior.
- [ ] Run the test and confirm failure because the new contracts do not exist.
- [ ] Implement the repository adapter over `AppPreferences`, Hilt binding, state model, and ViewModel.
- [ ] Run the new tests until passing, then compile.
- [ ] Commit as `refactor: move settings state into viewmodel`.

### Task 2.3: Make Settings content stateless and effects explicit

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/settings/SettingsScreen.kt`
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/settings/SettingsScreenEffects.kt`
- Modify if required: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/settings/SettingsViewModel.kt`
- Test: `app/src/test/java/eu/indiewalkabout/fridgemanager/feat_settings/presentation/ui/settings/SettingsViewModelTest.kt`

- [ ] Keep picker/dialog visibility as ephemeral UI state, but read persisted values from `SettingsUiState`.
- [ ] Route preference updates through `SettingsViewModel`; remove direct `AppPreferences` reads and writes from the composable.
- [ ] Represent rescheduling, consent, system settings, store, email, and toast operations as explicit callbacks or one-shot UI effects at the route boundary.
- [ ] Add tests proving each settings intent emits only the required state change/effect.
- [ ] Use lifecycle-aware state collection, compile, run settings tests, and manually verify notification frequency and reset flows.
- [ ] Commit as `refactor: separate settings content and effects`.

**Phase 2 gate:** Main and Settings have route/content separation, ViewModels own persisted screen state, framework actions stay at the boundary, targeted tests pass, and there is no direct `AppPreferences` access in `SettingsScreen.kt`.

---

## Phase 3: Tighten Domain Boundaries and Regression Coverage

Outcome: repository APIs expose stable immutable contracts, notification reads use use cases, and final tests protect navigation and presentation boundaries.

### Task 3.1: Make one-shot database read contracts immutable

**Files:**
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_food/data/local/db/FoodDbDao.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_food/domain/repository/FridgeManagerRepository.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_food/data/repository/FridgeManagerRepositoryImpl.kt`
- Test: `app/src/test/java/eu/indiewalkabout/fridgemanager/feat_food/data/repository/FridgeManagerRepositoryImplTest.kt`

- [ ] Change one-shot return types from `MutableList<FoodEntry>` to `List<FoodEntry>`; retain `Flow<List<FoodEntry>>` for reactive screen reads.
- [ ] Update repository fakes and tests to prove values pass through unchanged.
- [ ] Run `FridgeManagerRepositoryImplTest` and compile all production Kotlin.
- [ ] Commit as `refactor: expose immutable food read results`.

### Task 3.2: Put notification food queries behind use cases

**Files:**
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_food/domain/use_cases/LoadFoodExpiringForNotificationUseCase.kt`
- Create: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_food/domain/use_cases/LoadFoodExpiringTodayForNotificationUseCase.kt`
- Modify: `app/src/main/java/eu/indiewalkabout/fridgemanager/feat_notifications/domain/reminder/AlarmReceiver.kt`
- Test: `app/src/test/java/eu/indiewalkabout/fridgemanager/feat_food/domain/use_cases/LoadFoodForNotificationUseCasesTest.kt`

**Interface:**
- Each one-shot use case delegates to the repository and returns `List<FoodEntry>`.
- `AlarmReceiver` consumes injected use cases rather than calling `FridgeManagerRepository` directly; retain Hilt receiver compatibility.

- [ ] Write delegation tests for both use cases, including empty lists and date-bound arguments.
- [ ] Implement the use cases and replace direct repository calls in `AlarmReceiver`.
- [ ] Run the new tests and compile; trigger a debug notification path if available.
- [ ] Commit as `refactor: isolate notification food queries`.

### Task 3.3: Final regression and architecture cleanup

**Files:**
- Extend: `app/src/test/java/eu/indiewalkabout/fridgemanager/core/presentation/navigation/AppDestinationTest.kt`
- Extend: `app/src/test/java/eu/indiewalkabout/fridgemanager/core/presentation/navigation/AppNavigationStateTest.kt`
- Extend relevant reducer/ViewModel tests under `app/src/test/java/eu/indiewalkabout/fridgemanager/`
- Remove: `app/src/test/java/eu/indiewalkabout/fridgemanager/ExampleUnitTest.kt`
- Modify only verified cleanup targets found by `rg`.

- [ ] Add navigation tests for every destination, legacy notification route, top-level reselection, secondary back behavior, and saved-stack restoration.
- [ ] Add missing reducer/ViewModel regression cases discovered during Phases 1-3.
- [ ] Remove the generated example test and stale imports, comments, unused state, and duplicate constants in files touched by this refactor.
- [ ] Run `GRADLE_USER_HOME=/tmp/mapopinball-gradle ./gradlew :app:testDebugUnitTest :app:compileDebugKotlin`.
- [ ] Run `GRADLE_USER_HOME=/tmp/mapopinball-gradle ./gradlew :app:lintDebug` and record any pre-existing warnings separately from new violations.
- [ ] Manually smoke-test splash -> Main, all bottom tabs, add/update/delete food, Settings, Credits/back, notification deep-link, and process recreation.
- [ ] Commit as `test: complete refactor regression coverage`.

**Phase 3 gate:** all unit tests, debug compilation, and lint complete without new failures; manual smoke tests pass; direct repository access is limited to use cases/framework integration where intentionally documented.

---

## Completion Checklist

- [ ] Nine focused commits exist in the order listed above.
- [ ] Navigation 3 behavior and per-tab state restoration remain intact.
- [ ] Reactive Room reads remain `Flow`-based; one-shot background reads return immutable lists.
- [ ] Main and Settings composables separate route orchestration from stateless rendering.
- [ ] Settings no longer accesses global preferences directly.
- [ ] Notification queries cross a domain use-case boundary.
- [ ] Targeted tests, full unit tests, debug compilation, lint, and manual smoke checks pass.
- [ ] Final `git status --short` is clean.
