package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

import eu.indiewalkabout.fridgemanager.feat_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsViewModelTest {

    @Test
    fun `initial state reflects persisted settings`() {
        val repository = FakeSettingsRepository(daysBeforeDeadline = 4, dailyNotificationCount = 3)

        val viewModel = SettingsViewModel(repository)

        assertEquals(SettingsUiState(daysBeforeDeadline = 4, dailyNotificationCount = 3), viewModel.uiState.value)
    }

    @Test
    fun `updating days before deadline persists and updates state`() {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.updateDaysBeforeDeadline(6)

        assertEquals(6, repository.daysBeforeDeadline)
        assertEquals(6, viewModel.uiState.value.daysBeforeDeadline)
    }

    @Test
    fun `updating daily notification count persists and updates state`() {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.updateDailyNotificationCount(5)

        assertEquals(5, repository.dailyNotificationCount)
        assertEquals(5, viewModel.uiState.value.dailyNotificationCount)
    }

    @Test
    fun `updating daily notification count requests reminder reschedule`() = runBlocking {
        val viewModel = SettingsViewModel(FakeSettingsRepository())

        viewModel.updateDailyNotificationCount(5)

        assertEquals(SettingsUiEvent.RescheduleNotifications, viewModel.events.first())
    }

    @Test
    fun `updating days before deadline emits no framework effect`() = runBlocking {
        val viewModel = SettingsViewModel(FakeSettingsRepository())

        viewModel.updateDaysBeforeDeadline(6)

        assertEquals(null, withTimeoutOrNull(50) { viewModel.events.first() })
    }

    @Test
    fun `reset reloads repository defaults into state`() {
        val repository = FakeSettingsRepository(daysBeforeDeadline = 7, dailyNotificationCount = 6)
        val viewModel = SettingsViewModel(repository)

        viewModel.resetPreferences()

        assertEquals(2, repository.daysBeforeDeadline)
        assertEquals(1, repository.dailyNotificationCount)
        assertEquals(SettingsUiState(), viewModel.uiState.value)
    }

    @Test
    fun `reset requests app data cleanup`() = runBlocking {
        val viewModel = SettingsViewModel(FakeSettingsRepository())

        viewModel.resetPreferences()

        assertEquals(SettingsUiEvent.ClearAppData, viewModel.events.first())
    }

    private class FakeSettingsRepository(
        override var daysBeforeDeadline: Int = 2,
        override var dailyNotificationCount: Int = 1,
    ) : SettingsRepository {

        override fun updateDaysBeforeDeadline(value: Int) {
            daysBeforeDeadline = value
        }

        override fun updateDailyNotificationCount(value: Int) {
            dailyNotificationCount = value
        }

        override fun clear() {
            daysBeforeDeadline = 2
            dailyNotificationCount = 1
        }
    }
}
