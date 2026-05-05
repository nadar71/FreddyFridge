package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.ObserveAllFoodExpiredUseCase
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUpdateUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FoodExpiredViewModel @Inject constructor(
    private val observeAllFoodExpiredUseCase: ObserveAllFoodExpiredUseCase,
    ): ViewModel() {

    private val _uiState = MutableStateFlow(FoodListScreenUiState())
    val uiState: StateFlow<FoodListScreenUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FoodListScreenUiEvent>()
    val events: SharedFlow<FoodListScreenUiEvent> = _events.asSharedFlow()

    private var observeJob: Job? = null

    fun setBottomSheetVisible(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(isBottomSheetVisible = isVisible)
    }

    // Fetch expiring food
    fun getExpiredFood(referenceDate: Long) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.value = FoodListScreenUiStateReducer.reduceFoodListState(
                _uiState.value,
                FoodListUiState.Loading,
            )
            try {
                observeAllFoodExpiredUseCase(referenceDate).collectLatest { result: DbResponse<List<FoodEntry>> ->
                    val foodListState = when (result) {
                        is DbResponse.Success -> FoodListUiState.Success(result.data)
                        is DbResponse.Error -> FoodListUiState.Error(result.error)
                    }
                    _uiState.value = FoodListScreenUiStateReducer.reduceFoodListState(
                        _uiState.value,
                        foodListState,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FoodListScreenUiStateReducer.reduceFoodListState(
                    _uiState.value,
                    FoodListUiState.Error(
                        ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                    )
                )
            }
        }
    }

    fun handleInsertState(insertState: FoodUiState<Unit>) {
        applyTransition(FoodListScreenUiStateReducer.reduceInsertState(_uiState.value, insertState))
    }

    fun handleUpdateState(updateState: FoodUpdateUiState<Unit>) {
        applyTransition(FoodListScreenUiStateReducer.reduceUpdateState(_uiState.value, updateState))
    }

    private fun applyTransition(transition: FoodListScreenUiTransition) {
        _uiState.value = transition.state
        if (transition.events.isEmpty()) return

        viewModelScope.launch {
            transition.events.forEach { event ->
                _events.emit(event)
            }
        }
    }

}
