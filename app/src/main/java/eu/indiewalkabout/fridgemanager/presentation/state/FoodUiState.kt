package eu.indiewalkabout.fridgemanager.presentation.state

import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse

sealed class FoodUiState<out T> {
    object Idle : FoodUiState<Nothing>()
    object Loading : FoodUiState<Nothing>()
    data class Success<out T>(val data: T) : FoodUiState<T>()
    data class Error(val error: ErrorResponse) : FoodUiState<Nothing>()
}