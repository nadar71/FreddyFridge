package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.InsertFoodEntryUseCase
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InsertFoodViewModel @Inject constructor(
    private val insertFoodEntryUseCase: InsertFoodEntryUseCase,
    ): ViewModel() {

    private val _unitUiState = MutableStateFlow<FoodUiState<Unit>>(FoodUiState.Idle)
    val unitUiState: StateFlow<FoodUiState<Unit>> = _unitUiState.asStateFlow()

    // Reset updateUiState to idle
    fun resetUpdateUiStateToIdle() {
        _unitUiState.value = FoodUiState.Idle
    }

    // Insert food entry
    fun insertFood(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _unitUiState.value = FoodUiState.Loading
            try {
                val result: DbResponse<Unit> = insertFoodEntryUseCase(foodEntry) // Assuming insert returns DbResponse<Unit>
                _unitUiState.value = when (result) {
                    is DbResponse.Success -> FoodUiState.Success(result.data) // result.data is Unit
                    is DbResponse.Error -> FoodUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _unitUiState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

}
