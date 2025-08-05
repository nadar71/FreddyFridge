package eu.indiewalkabout.fridgemanager.feat_food.presentation.state

import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse

sealed class FoodListUiState<out T> {
    object Idle : FoodListUiState<Nothing>()
    object Loading : FoodListUiState<Nothing>()
    data class Success<out T>(val data: T) : FoodListUiState<T>()
    data class Error(val error: ErrorResponse) : FoodListUiState<Nothing>()
}