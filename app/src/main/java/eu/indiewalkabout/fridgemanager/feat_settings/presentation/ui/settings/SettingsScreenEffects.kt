package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun SettingsScreenEffects(
    events: Flow<SettingsUiEvent>,
    onRescheduleNotifications: () -> Unit,
    onClearAppData: () -> Unit,
) {
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                SettingsUiEvent.RescheduleNotifications -> onRescheduleNotifications()
                SettingsUiEvent.ClearAppData -> onClearAppData()
            }
        }
    }
}
