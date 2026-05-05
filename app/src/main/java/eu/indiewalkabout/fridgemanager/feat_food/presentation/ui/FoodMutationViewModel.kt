package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.DeleteFoodEntryUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.UpdateFoodEntryUseCase
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUpdateUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodMutationViewModel @Inject constructor(
    private val updateFoodEntryUseCase: UpdateFoodEntryUseCase,
    private val deleteFoodEntryUseCase: DeleteFoodEntryUseCase,
) : ViewModel() {

    private val tag = "FoodMutationViewModel"

    private val _updateUiState = MutableStateFlow<FoodUpdateUiState<Unit>>(FoodUpdateUiState.Idle)
    val updateUiState: StateFlow<FoodUpdateUiState<Unit>> = _updateUiState.asStateFlow()

    fun resetUpdateUiStateToIdle() {
        _updateUiState.value = FoodUpdateUiState.Idle
    }

    fun updateFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _updateUiState.value = FoodUpdateUiState.Loading
            try {
                val result: DbResponse<Unit> = updateFoodEntryUseCase(foodEntry)
                Log.d(tag, "updateFoodEntry: $result from db operation")
                _updateUiState.value = when (result) {
                    is DbResponse.Success -> FoodUpdateUiState.Success(result.data)
                    is DbResponse.Error -> FoodUpdateUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _updateUiState.value = FoodUpdateUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    fun deleteFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _updateUiState.value = FoodUpdateUiState.Loading
            try {
                val result: DbResponse<Unit> = deleteFoodEntryUseCase(foodEntry)
                _updateUiState.value = when (result) {
                    is DbResponse.Success -> FoodUpdateUiState.Success(result.data)
                    is DbResponse.Error -> FoodUpdateUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _updateUiState.value = FoodUpdateUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }
}
