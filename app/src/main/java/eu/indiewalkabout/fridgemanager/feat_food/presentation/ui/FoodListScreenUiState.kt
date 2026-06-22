package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry

data class FoodListScreenUiState(
    val foods: List<FoodEntry> = emptyList(),
    val hasLoadedFood: Boolean = false,
    val isFoodListLoading: Boolean = false,
    val isOperationLoading: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
) {
    val isLoading: Boolean
        get() = isFoodListLoading || isOperationLoading
}

sealed interface FoodListScreenUiEvent {
    data class ShowToast(val messageResId: Int) : FoodListScreenUiEvent
    data object RefreshExpiringNotifications : FoodListScreenUiEvent
}

data class FoodListScreenUiTransition(
    val state: FoodListScreenUiState,
    val events: List<FoodListScreenUiEvent> = emptyList(),
)
