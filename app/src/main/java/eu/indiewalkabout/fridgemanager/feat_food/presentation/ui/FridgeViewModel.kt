package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.DeleteFoodEntryUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.DropTableUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.InsertFoodEntryUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadAllFoodUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadConsumedFoodUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadExpiredFoodUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadExpiringFoodUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadFoodByIdUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadFoodExpiringTodayUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.UpdateDoneFieldUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.UpdateFoodEntryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val loadAllFoodUseCase: LoadAllFoodUseCase,
    private val loadExpiringFoodUseCase: LoadExpiringFoodUseCase,
    private val loadFoodExpiringTodayUseCase: LoadFoodExpiringTodayUseCase,
    private val loadExpiredFoodUseCase: LoadExpiredFoodUseCase,
    private val loadConsumedFoodUseCase: LoadConsumedFoodUseCase,
    private val loadFoodByIdUseCase: LoadFoodByIdUseCase,
    private val insertFoodEntryUseCase: InsertFoodEntryUseCase,
    private val updateFoodEntryUseCase: UpdateFoodEntryUseCase,
    private val updateDoneFieldUseCase: UpdateDoneFieldUseCase,
    private val deleteFoodEntryUseCase: DeleteFoodEntryUseCase,
    private val dropTableUseCase: DropTableUseCase
) : ViewModel() {

    // UI State Sealed Class
    sealed class FoodUiState<out T> {
        object Idle : FoodUiState<Nothing>()
        object Loading : FoodUiState<Nothing>()
        data class Success<out T>(val data: T) : FoodUiState<T>()
        data class Error(val error: ErrorResponse) : FoodUiState<Nothing>()
    }

    // StateFlow for food list and operations
    private val _foodState = MutableStateFlow<FoodUiState<List<FoodEntry>>>(FoodUiState.Idle)
    val foodState: StateFlow<FoodUiState<List<FoodEntry>>> = _foodState.asStateFlow()

    private val _foodByIdState = MutableStateFlow<FoodUiState<FoodEntry>>(FoodUiState.Idle)
    val foodByIdState: StateFlow<FoodUiState<FoodEntry>> = _foodByIdState.asStateFlow()

    private val _operationState = MutableStateFlow<FoodUiState<Unit>>(FoodUiState.Idle)
    val operationState: StateFlow<FoodUiState<Unit>> = _operationState.asStateFlow()

    // Fetch all food
    fun getAllFood() {
        viewModelScope.launch {
            _foodState.value = FoodUiState.Loading
            try {
                val response = loadAllFoodUseCase()
                _foodState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(response.data)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _foodState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Fetch expiring food
    fun getExpiringFood(date: Long?) {
        viewModelScope.launch {
            _foodState.value = FoodUiState.Loading
            try {
                val response = loadExpiringFoodUseCase(date)
                _foodState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(response.data)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _foodState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Fetch food expiring today
    fun getFoodExpiringToday(dayBefore: Long?, dayAfter: Long?) {
        viewModelScope.launch {
            _foodState.value = FoodUiState.Loading
            try {
                val response = loadFoodExpiringTodayUseCase(dayBefore, dayAfter)
                _foodState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(response.data)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _foodState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Fetch expired food
    fun getExpiredFood(date: Long?) {
        viewModelScope.launch {
            _foodState.value = FoodUiState.Loading
            try {
                val response = loadExpiredFoodUseCase(date)
                _foodState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(response.data)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _foodState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Fetch consumed food
    fun getConsumedFood() {
        viewModelScope.launch {
            _foodState.value = FoodUiState.Loading
            try {
                val response = loadConsumedFoodUseCase()
                _foodState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(response.data)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _foodState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Fetch food by ID
    fun getFoodById(id: Int) {
        viewModelScope.launch {
            _foodByIdState.value = FoodUiState.Loading
            try {
                val response = loadFoodByIdUseCase(id)
                _foodByIdState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(response.data)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _foodByIdState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Insert food entry
    fun insertFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _operationState.value = FoodUiState.Loading
            try {
                val response = insertFoodEntryUseCase(foodEntry)
                _operationState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(Unit)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _operationState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Update food entry
    fun updateFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _operationState.value = FoodUiState.Loading
            try {
                val response = updateFoodEntryUseCase(foodEntry)
                _operationState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(Unit)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _operationState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Update done field
    fun updateDoneField(done: Int, id: Int) {
        viewModelScope.launch {
            _operationState.value = FoodUiState.Loading
            try {
                val response = updateDoneFieldUseCase(done, id)
                _operationState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(Unit)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _operationState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Delete food entry
    fun deleteFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _operationState.value = FoodUiState.Loading
            try {
                val response = deleteFoodEntryUseCase(foodEntry)
                _operationState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(Unit)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _operationState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }

    // Drop table
    fun dropTable() {
        viewModelScope.launch {
            _operationState.value = FoodUiState.Loading
            try {
                val response = dropTableUseCase()
                _operationState.value = when (response) {
                    is DbResponse.Success -> FoodUiState.Success(Unit)
                    is DbResponse.Error -> FoodUiState.Error(response.error)
                }
            } catch (e: Exception) {
                _operationState.value = FoodUiState.Error(
                    ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                )
            }
        }
    }
}