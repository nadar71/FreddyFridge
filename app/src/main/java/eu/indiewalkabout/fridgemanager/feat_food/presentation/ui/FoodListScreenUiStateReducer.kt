package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUpdateUiState

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

    fun reduceInsertState(
        currentState: FoodListScreenUiState,
        insertState: FoodUiState<Unit>,
    ): FoodListScreenUiTransition {
        return when (insertState) {
            FoodUiState.Idle -> FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )

            FoodUiState.Loading -> FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = true)
            )

            is FoodUiState.Success -> FoodListScreenUiTransition(
                state = currentState.copy(
                    isOperationLoading = false,
                    isBottomSheetVisible = false,
                ),
                events = listOf(
                    FoodListScreenUiEvent.ShowToast(R.string.insert_food_successfully),
                    FoodListScreenUiEvent.RefreshExpiringNotifications,
                ),
            )

            is FoodUiState.Error -> FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )
        }
    }

    fun reduceUpdateState(
        currentState: FoodListScreenUiState,
        updateState: FoodUpdateUiState<Unit>,
    ): FoodListScreenUiTransition {
        return when (updateState) {
            FoodUpdateUiState.Idle -> FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )

            FoodUpdateUiState.Loading -> FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = true)
            )

            is FoodUpdateUiState.Success -> FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = false),
                events = listOf(FoodListScreenUiEvent.ShowToast(R.string.update_food_successfully)),
            )

            is FoodUpdateUiState.Error -> FoodListScreenUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )
        }
    }
}
