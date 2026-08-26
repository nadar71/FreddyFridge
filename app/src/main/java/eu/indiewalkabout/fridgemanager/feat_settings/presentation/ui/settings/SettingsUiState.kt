package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

data class SettingsUiState(
    val daysBeforeDeadline: Int = 2,
    val dailyNotificationCount: Int = 1,
)
