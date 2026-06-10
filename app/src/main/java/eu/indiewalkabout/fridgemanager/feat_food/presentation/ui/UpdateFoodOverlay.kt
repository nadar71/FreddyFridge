package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalDateFormat
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntryUI
import java.time.LocalDate
import java.util.TimeZone

@Composable
fun UpdateFoodOverlay(
    foodViewModel: FoodMutationViewModel = hiltViewModel(),
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel(),
    foodEntryUI: FoodEntryUI,
    cancelable: Boolean = true,
    onLeftButtonAction: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = { if (cancelable) onLeftButtonAction?.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = cancelable,
            dismissOnClickOutside = cancelable
        )
    ) {
        FoodEditorExperience(
            initialState = FoodEditorUiState.fromFoodEntryUI(foodEntryUI),
            titleResId = R.string.insert_food_title,
            onSubmit = { editorState ->
                val submission = FoodEntrySubmissionBuilder.buildUpdateSubmission(
                    originalEntry = FoodEntry(
                        id = foodEntryUI.id,
                        name = foodEntryUI.name,
                        expiringAt = foodEntryUI.expiringAtLocalDate,
                        consumedAt = foodEntryUI.consumedAtLocalDate,
                        timezoneId = foodEntryUI.timezoneId,
                        isProductOpen = foodEntryUI.isProductOpen,
                        done = foodEntryUI.done,
                    ),
                    updatedName = editorState.description,
                    updatedExpiringAt = editorState.expiringAt,
                    timezoneId = TimeZone.getDefault().id,
                    quantity = editorState.quantity,
                )

                foodViewModel.updateFoodEntry(submission.entryToUpdate)
                insertFoodViewModel.insertFoods(submission.additionalEntries)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateFoodOverlayPreview() {
    FreddyFridgeTheme {
        UpdateFoodOverlay(
            foodEntryUI = FoodEntryUI(
                id = 1,
                name = "Expired Food",
                expiringAtLocalDate = LocalDate.now().minusDays(1),
                expiringAtUI = LocalDate.now().minusDays(1).format(getLocalDateFormat()),
            ),
            onLeftButtonAction = {}
        )
    }
}
