package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.DeleteFoodEntryUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.DropTableUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadAllFoodUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadFoodByIdUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.UpdateDoneFieldUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.UpdateFoodEntryUseCase
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO: extract insert, then check the others
@HiltViewModel
class FoodViewModel @Inject constructor(
    private val updateFoodEntryUseCase: UpdateFoodEntryUseCase,
    private val deleteFoodEntryUseCase: DeleteFoodEntryUseCase,
    private val dropTableUseCase: DropTableUseCase,
    private val loadFoodByIdUseCase: LoadFoodByIdUseCase,
    private val updateDoneFieldUseCase: UpdateDoneFieldUseCase,
    private val loadAllFoodUseCase: LoadAllFoodUseCase,
) : ViewModel() {

    private val _foodListUiState = MutableStateFlow<FoodListUiState<List<FoodEntry>>>(FoodListUiState.Idle)
    val foodListUiState: StateFlow<FoodListUiState<List<FoodEntry>>> = _foodListUiState.asStateFlow()

    private val _foodUiState = MutableStateFlow<FoodUiState<FoodEntry>>(FoodUiState.Idle)
    val foodUiState: StateFlow<FoodUiState<FoodEntry>> = _foodUiState.asStateFlow()

    private val _updateUiState = MutableStateFlow<FoodUiState<Unit>>(FoodUiState.Idle)
    val updateUiState: StateFlow<FoodUiState<Unit>> = _updateUiState.asStateFlow()

    // Update food entry
    fun updateFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _updateUiState.value = FoodUiState.Loading
            try {
                val result: DbResponse<Unit> = updateFoodEntryUseCase(foodEntry) // Assuming update returns DbResponse<Unit>
                _updateUiState.value = when (result) {
                    is DbResponse.Success -> FoodUiState.Success(result.data) // result.data is Unit
                    is DbResponse.Error -> FoodUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _updateUiState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Delete food entry
    fun deleteFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _updateUiState.value = FoodUiState.Loading
            try {
                val result: DbResponse<Unit> = deleteFoodEntryUseCase(foodEntry) // Assuming delete returns DbResponse<Unit>
                _updateUiState.value = when (result) {
                    is DbResponse.Success -> FoodUiState.Success(result.data) // result.data is Unit
                    is DbResponse.Error -> FoodUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _updateUiState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Reset updateUiState to idle
    fun resetUpdateUiStateToIdle() {
        _updateUiState.value = FoodUiState.Idle
    }

    // Drop table
    fun dropTable() {
        viewModelScope.launch {
            _updateUiState.value = FoodUiState.Loading
            try {
                val result: DbResponse<Unit> = dropTableUseCase() // Assuming drop returns DbResponse<Unit>
                _updateUiState.value = when (result) {
                    is DbResponse.Success -> FoodUiState.Success(result.data) // result.data is Unit
                    is DbResponse.Error -> FoodUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _updateUiState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Fetch food by ID
    fun getFoodById(id: Int) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            try {
                val result: DbResponse<FoodEntry?> = loadFoodByIdUseCase(id) // Assuming load by ID returns DbResponse<FoodEntry?>
                _foodUiState.value = when (result) {
                    is DbResponse.Success -> FoodUiState.Success(result.data) as FoodUiState<FoodEntry>
                    is DbResponse.Error -> FoodUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _foodUiState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Update done field
    fun updateDoneField(done: Int, id: Int) {
        viewModelScope.launch {
            _updateUiState.value = FoodUiState.Loading
            try {
                val result: DbResponse<Unit> = updateDoneFieldUseCase(done, id) // Assuming update done returns DbResponse<Unit>
                _updateUiState.value = when (result) {
                    is DbResponse.Success -> FoodUiState.Success(result.data)
                    is DbResponse.Error -> FoodUiState.Error(result.error)
                }
            } catch (e: Exception) {
                _updateUiState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Fetch all food
    fun getAllFood() {
        viewModelScope.launch {
            _foodListUiState.value = FoodListUiState.Loading
            try {
                val result: DbResponse<List<FoodEntry>> = loadAllFoodUseCase() // Assuming load all returns DbResponse<List<FoodEntry>>
                _foodListUiState.value = when (result) {
                    is DbResponse.Success -> FoodListUiState.Success(result.data)
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
