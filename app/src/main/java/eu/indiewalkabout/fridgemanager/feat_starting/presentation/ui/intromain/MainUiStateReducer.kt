package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState

object MainUiStateReducer {

    fun reduceFoodListState(
        currentState: MainUiState,
        foodListState: FoodListUiState<List<FoodEntry>>,
    ): MainUiState {
        return when (foodListState) {
            FoodListUiState.Idle -> currentState.copy(isFoodListLoading = false)
            FoodListUiState.Loading -> currentState.copy(isFoodListLoading = true)
            is FoodListUiState.Success -> currentState.copy(
                foods = foodListState.data,
                hasLoadedFood = true,
                isFoodListLoading = false,
            )

            is FoodListUiState.Error -> currentState.copy(
                hasLoadedFood = true,
                isFoodListLoading = false,
            )
        }
    }

    fun reduceInsertLoading(
        currentState: MainUiState,
        isLoading: Boolean,
    ): MainUiTransition {
        return MainUiTransition(
            state = currentState.copy(isOperationLoading = isLoading)
        )
    }

    fun reduceInsertResult(
        currentState: MainUiState,
        isSuccess: Boolean,
    ): MainUiTransition {
        return if (isSuccess) {
            MainUiTransition(
                state = currentState.copy(
                    isOperationLoading = false,
                    isBottomSheetVisible = false,
                ),
                events = listOf(
                    MainUiEvent.ShowToast(R.string.insert_food_successfully),
                    MainUiEvent.RefreshExpiringNotifications,
                ),
            )
        } else {
            MainUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )
        }
    }

    fun reduceUpdateLoading(
        currentState: MainUiState,
        isLoading: Boolean,
    ): MainUiTransition {
        return MainUiTransition(
            state = currentState.copy(isOperationLoading = isLoading)
        )
    }

    fun reduceUpdateResult(
        currentState: MainUiState,
        isSuccess: Boolean,
    ): MainUiTransition {
        return if (isSuccess) {
            MainUiTransition(
                state = currentState.copy(isOperationLoading = false),
                events = listOf(MainUiEvent.ShowToast(R.string.update_food_successfully)),
            )
        } else {
            MainUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )
        }
    }
}
