package eu.indiewalkabout.fridgemanager.feat_food.presentation.state

import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse

sealed class FoodUpdateUiState<out T> {
    object Idle : FoodUpdateUiState<Nothing>()
    object Loading : FoodUpdateUiState<Nothing>()
    data class Success<out T>(val data: T) : FoodUpdateUiState<T>()
    data class Error(val error: ErrorResponse) : FoodUpdateUiState<Nothing>()
}