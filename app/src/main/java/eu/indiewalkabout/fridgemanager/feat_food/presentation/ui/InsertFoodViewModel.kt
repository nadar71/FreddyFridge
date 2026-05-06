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

    private val _insertUiState = MutableStateFlow<FoodUiState<Unit>>(FoodUiState.Idle)
    val insertUiState: StateFlow<FoodUiState<Unit>> = _insertUiState.asStateFlow()

    fun resetInsertUiStateToIdle() {
        _insertUiState.value = FoodUiState.Idle
    }

    fun insertFood(foodEntry: FoodEntry) {
        insertFoods(listOf(foodEntry))
    }

    fun insertFoods(foodEntries: List<FoodEntry>) {
        viewModelScope.launch {
            _insertUiState.value = FoodUiState.Loading
            try {
                var failure: DbResponse.Error? = null
                for (foodEntry in foodEntries) {
                    when (val result = insertFoodEntryUseCase(foodEntry)) {
                        is DbResponse.Success -> Unit
                        is DbResponse.Error -> {
                            failure = result
                            break
                        }
                    }
                }

                _insertUiState.value = if (failure != null) {
                    FoodUiState.Error(failure.error)
                } else {
                    FoodUiState.Success(Unit)
                }
            } catch (e: Exception) {
                _insertUiState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }
}
