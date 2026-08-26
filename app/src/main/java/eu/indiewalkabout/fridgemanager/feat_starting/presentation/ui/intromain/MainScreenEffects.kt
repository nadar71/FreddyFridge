package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.FoodMutationEvent
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.InsertFoodEvent
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun MainScreenEffects(
    isInserting: Boolean,
    isMutating: Boolean,
    insertEvents: SharedFlow<InsertFoodEvent>,
    mutationEvents: SharedFlow<FoodMutationEvent>,
    screenEvents: SharedFlow<MainUiEvent>,
    onInsertLoading: (Boolean) -> Unit,
    onInsertResult: (Boolean) -> Unit,
    onUpdateLoading: (Boolean) -> Unit,
    onUpdateResult: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(isMutating) {
        onUpdateLoading(isMutating)
    }

    LaunchedEffect(Unit) {
        mutationEvents.collect { event ->
            onUpdateResult(event is FoodMutationEvent.Success)
        }
    }

    LaunchedEffect(isInserting) {
        onInsertLoading(isInserting)
    }

    LaunchedEffect(Unit) {
        insertEvents.collect { event ->
            onInsertResult(event is InsertFoodEvent.Success)
        }
    }

    LaunchedEffect(Unit) {
        screenEvents.collect { event ->
            when (event) {
                is MainUiEvent.ShowToast -> Toast.makeText(
                    context,
                    context.getString(event.messageResId),
                    Toast.LENGTH_SHORT,
                ).show()

                MainUiEvent.RefreshExpiringNotifications -> {
                    alarmReminderScheduler.setRepeatingAlarm()
                }
            }
        }
    }
}
