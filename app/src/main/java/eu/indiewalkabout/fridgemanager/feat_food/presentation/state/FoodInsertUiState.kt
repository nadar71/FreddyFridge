package eu.indiewalkabout.fridgemanager.feat_food.presentation.state

import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse

sealed class FoodInsertUiState<out T> {
    object Idle : FoodInsertUiState<Nothing>()
    object Loading : FoodInsertUiState<Nothing>()
    data class Success<out T>(val data: T) : FoodInsertUiState<T>()
    data class Error(val error: ErrorResponse) : FoodInsertUiState<Nothing>()
}