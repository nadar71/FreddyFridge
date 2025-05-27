package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadExpiredFoodUseCase
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FoodExpiredViewModel @Inject constructor(
    private val loadExpiredFoodUseCase: LoadExpiredFoodUseCase,
    ): ViewModel() {

    private val _foodListUiState = MutableStateFlow<FoodListUiState<List<FoodEntry>>>(FoodListUiState.Idle)
    val foodListUiState: StateFlow<FoodListUiState<List<FoodEntry>>> = _foodListUiState.asStateFlow()

    // Fetch expiring food
    fun getExpiredFood(referenceDate: Long) {
        viewModelScope.launch {
            _foodListUiState.value = FoodListUiState.Loading
            // Assuming loadExpiredFoodUseCase returns DbResponse<List<FoodEntry>>
            try {
                val result: DbResponse<List<FoodEntry>> = loadExpiredFoodUseCase(referenceDate)
                _foodListUiState.value = when (result) {
                    is DbResponse.Success -> FoodListUiState.Success(result.data) // result.data is List<FoodEntry>
                    is DbResponse.Error -> FoodListUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _foodListUiState.value = FoodListUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

}
