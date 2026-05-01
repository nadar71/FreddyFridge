package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.ObserveFoodExpiringTodayUseCase
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeFoodExpiringTodayUseCase: ObserveFoodExpiringTodayUseCase
): ViewModel() {

    private val _foodListUiState = MutableStateFlow<FoodListUiState<List<FoodEntry>>>(FoodListUiState.Idle)
    val foodListUiState: StateFlow<FoodListUiState<List<FoodEntry>>> = _foodListUiState.asStateFlow()
    private var observeJob: Job? = null


    // Fetch food expiring today: from 23:59:59.999 of dayBefore to 23:59:59.999 of dayAfter
    fun getFoodExpiringToday(dayBefore: Long?, dayAfter: Long?) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _foodListUiState.value = FoodListUiState.Loading
            try {
                observeFoodExpiringTodayUseCase(dayBefore, dayAfter).collectLatest { result: DbResponse<List<FoodEntry>> ->
                    _foodListUiState.value = when (result) {
                        is DbResponse.Success -> FoodListUiState.Success(result.data)
                        is DbResponse.Error -> FoodListUiState.Error(result.error)
                    }
                }
            } catch (e: Exception) {
                _foodListUiState.value = FoodListUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

}
