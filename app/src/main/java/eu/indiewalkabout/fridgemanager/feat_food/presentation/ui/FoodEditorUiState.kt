package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntryUI
import java.time.LocalDate

data class FoodEditorUiState(
    val expiringAt: LocalDate? = null,
    val expiringAtText: String = "",
    val description: String = "",
    val quantityText: String = "1",
) {
    val quantity: Int
        get() = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1

    val canSubmit: Boolean
        get() = expiringAtText.isNotEmpty() && description.isNotEmpty()

    companion object {
        fun empty(): FoodEditorUiState = FoodEditorUiState()

        fun fromFoodEntryUI(foodEntryUI: FoodEntryUI): FoodEditorUiState = FoodEditorUiState(
            expiringAt = foodEntryUI.expiringAtLocalDate,
            expiringAtText = foodEntryUI.expiringAtUI.orEmpty(),
            description = foodEntryUI.name.orEmpty(),
        )
    }
}
