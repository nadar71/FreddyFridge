package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.feat_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

sealed interface SettingsUiEvent {
    data object RescheduleNotifications : SettingsUiEvent
    data object ClearAppData : SettingsUiEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(loadState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    private val eventChannel = Channel<SettingsUiEvent>(capacity = Channel.BUFFERED)
    val events: Flow<SettingsUiEvent> = eventChannel.receiveAsFlow()

    fun updateDaysBeforeDeadline(value: Int) {
        settingsRepository.updateDaysBeforeDeadline(value)
        _uiState.value = _uiState.value.copy(daysBeforeDeadline = value)
    }

    fun updateDailyNotificationCount(value: Int) {
        settingsRepository.updateDailyNotificationCount(value)
        _uiState.value = _uiState.value.copy(dailyNotificationCount = value)
        eventChannel.trySend(SettingsUiEvent.RescheduleNotifications)
    }

    fun resetPreferences() {
        settingsRepository.clear()
        _uiState.value = loadState()
        eventChannel.trySend(SettingsUiEvent.ClearAppData)
    }

    private fun loadState(): SettingsUiState {
        return SettingsUiState(
            daysBeforeDeadline = settingsRepository.daysBeforeDeadline,
            dailyNotificationCount = settingsRepository.dailyNotificationCount,
        )
    }
}
