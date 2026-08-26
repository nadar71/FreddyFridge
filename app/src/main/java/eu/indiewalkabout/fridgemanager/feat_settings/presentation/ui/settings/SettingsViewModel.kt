package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.feat_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(loadState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateDaysBeforeDeadline(value: Int) {
        settingsRepository.updateDaysBeforeDeadline(value)
        _uiState.value = _uiState.value.copy(daysBeforeDeadline = value)
    }

    fun updateDailyNotificationCount(value: Int) {
        settingsRepository.updateDailyNotificationCount(value)
        _uiState.value = _uiState.value.copy(dailyNotificationCount = value)
    }

    fun resetPreferences() {
        settingsRepository.clear()
        _uiState.value = loadState()
    }

    private fun loadState(): SettingsUiState {
        return SettingsUiState(
            daysBeforeDeadline = settingsRepository.daysBeforeDeadline,
            dailyNotificationCount = settingsRepository.dailyNotificationCount,
        )
    }
}
