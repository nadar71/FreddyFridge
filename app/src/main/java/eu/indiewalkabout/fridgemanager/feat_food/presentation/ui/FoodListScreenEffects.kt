package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun FoodListScreenEffects(
    isInserting: Boolean,
    isMutating: Boolean,
    insertEvents: SharedFlow<InsertFoodEvent>,
    mutationEvents: SharedFlow<FoodMutationEvent>,
    screenEvents: SharedFlow<FoodListScreenUiEvent>,
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
            when (event) {
                FoodMutationEvent.Success -> onUpdateResult(true)
                is FoodMutationEvent.Error -> onUpdateResult(false)
            }
        }
    }

    LaunchedEffect(isInserting) {
        onInsertLoading(isInserting)
    }

    LaunchedEffect(Unit) {
        insertEvents.collect { event ->
            when (event) {
                InsertFoodEvent.Success -> onInsertResult(true)
                is InsertFoodEvent.Error -> onInsertResult(false)
            }
        }
    }

    LaunchedEffect(Unit) {
        screenEvents.collect { event ->
            when (event) {
                is FoodListScreenUiEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        context.getString(event.messageResId),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                FoodListScreenUiEvent.RefreshExpiringNotifications -> {
                    alarmReminderScheduler.setRepeatingAlarm()
                }
            }
        }
    }
}
