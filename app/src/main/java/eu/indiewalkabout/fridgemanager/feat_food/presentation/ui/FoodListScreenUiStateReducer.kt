package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState

object FoodListScreenUiStateReducer {

    fun reduceFoodListState(
        currentState: FoodListScreenUiState,
        foodListState: FoodListUiState<List<FoodEntry>>,
    ): FoodListScreenUiState {
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
        currentState: FoodListScreenUiState,
        isLoading: Boolean,
    ): FoodListScreenUiTransition {
        return FoodListScreenUiTransition(
            state = currentState.copy(isOperationLoading = isLoading)
        )
    }

    fun reduceInsertResult(
        currentState: FoodListScreenUiState,
        isSuccess: Boolean,
    ): FoodListScreenUiTransition {
        return if (isSuccess) {
            FoodListScreenUiTransition(
                state = currentState.copy(
                    isOperationLoading = false,
                    isBottomSheetVisible = false,
                ),
                events = listOf(
                    FoodListScreenUiEvent.ShowToast(R.string.insert_food_successfully),
                    FoodListScreenUiEvent.RefreshExpiringNotifications,
                ),
            )
        } else {
            FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )
        }
    }

    fun reduceUpdateLoading(
        currentState: FoodListScreenUiState,
        isLoading: Boolean,
    ): FoodListScreenUiTransition {
        return FoodListScreenUiTransition(
            state = currentState.copy(isOperationLoading = isLoading)
        )
    }

    fun reduceUpdateResult(
        currentState: FoodListScreenUiState,
        isSuccess: Boolean,
    ): FoodListScreenUiTransition {
        return if (isSuccess) {
            FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = false),
                events = listOf(FoodListScreenUiEvent.ShowToast(R.string.update_food_successfully)),
            )
        } else {
            FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )
        }
    }
}
