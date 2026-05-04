package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry

data class MainUiState(
    val foods: List<FoodEntry> = emptyList(),
    val hasLoadedFood: Boolean = false,
    val isFoodListLoading: Boolean = false,
    val isOperationLoading: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val showOnBoarding: Boolean = false,
) {
    val isLoading: Boolean
        get() = isFoodListLoading || isOperationLoading
}

sealed interface MainUiEvent {
    data class ShowToast(val messageResId: Int) : MainUiEvent
    data object RefreshExpiringNotifications : MainUiEvent
}

data class MainUiTransition(
    val state: MainUiState,
    val events: List<MainUiEvent> = emptyList(),
)
