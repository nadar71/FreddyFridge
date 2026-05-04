package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUpdateUiState

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

    fun reduceInsertState(
        currentState: MainUiState,
        insertState: FoodUiState<Unit>,
    ): MainUiTransition {
        return when (insertState) {
            FoodUiState.Idle -> MainUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )

            FoodUiState.Loading -> MainUiTransition(
                state = currentState.copy(isOperationLoading = true)
            )

            is FoodUiState.Success -> MainUiTransition(
                state = currentState.copy(
                    isOperationLoading = false,
                    isBottomSheetVisible = false,
                ),
                events = listOf(
                    MainUiEvent.ShowToast(R.string.insert_food_successfully),
                    MainUiEvent.RefreshExpiringNotifications,
                ),
            )

            is FoodUiState.Error -> MainUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )
        }
    }

    fun reduceUpdateState(
        currentState: MainUiState,
        updateState: FoodUpdateUiState<Unit>,
    ): MainUiTransition {
        return when (updateState) {
            FoodUpdateUiState.Idle -> MainUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )

            FoodUpdateUiState.Loading -> MainUiTransition(
                state = currentState.copy(isOperationLoading = true)
            )

            is FoodUpdateUiState.Success -> MainUiTransition(
                state = currentState.copy(isOperationLoading = false),
                events = listOf(MainUiEvent.ShowToast(R.string.update_food_successfully)),
            )

            is FoodUpdateUiState.Error -> MainUiTransition(
                state = currentState.copy(isOperationLoading = false)
            )
        }
    }
}
