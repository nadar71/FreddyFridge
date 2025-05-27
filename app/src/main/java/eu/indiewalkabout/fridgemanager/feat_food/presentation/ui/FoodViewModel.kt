package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
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
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val insertFoodEntryUseCase: InsertFoodEntryUseCase,
    private val updateFoodEntryUseCase: UpdateFoodEntryUseCase,
    private val deleteFoodEntryUseCase: DeleteFoodEntryUseCase,
    private val dropTableUseCase: DropTableUseCase,
    private val loadFoodByIdUseCase: LoadFoodByIdUseCase,
    private val updateDoneFieldUseCase: UpdateDoneFieldUseCase,
    private val loadAllFoodUseCase: LoadAllFoodUseCase,
    private val loadConsumedFoodUseCase: LoadConsumedFoodUseCase,
    private val loadExpiredFoodUseCase: LoadExpiredFoodUseCase,
    private val loadExpiringFoodUseCase: LoadExpiringFoodUseCase,
    private val loadFoodExpiringTodayUseCase: LoadFoodExpiringTodayUseCase
) : ViewModel() {

    private val _foodListUiState = MutableStateFlow<FoodListUiState<List<FoodEntry>>>(FoodListUiState.Idle)
    val foodListUiState: StateFlow<FoodListUiState<List<FoodEntry>>> = _foodListUiState.asStateFlow()

    private val _foodUiState = MutableStateFlow<FoodUiState<FoodEntry>>(FoodUiState.Idle)
    val foodUiState: StateFlow<FoodUiState<FoodEntry>> = _foodUiState.asStateFlow()

    private val _unitUiState = MutableStateFlow<FoodUiState<Unit>>(FoodUiState.Idle)
    val unitUiState: StateFlow<FoodUiState<Unit>> = _unitUiState.asStateFlow()

    fun insertFood(food: FoodEntry) {
        viewModelScope.launch {
            _unitUiState.value = FoodUiState.Loading
            val result: DbResponse<Unit> = insertFoodEntryUseCase(food) // Assuming insert returns DbResponse<Unit>
            _unitUiState.value = when (result) {
                is DbResponse.Success -> FoodUiState.Success(result.data) // result.data is Unit
                is DbResponse.Error -> FoodUiState.Error(result.error)
            }
        }
    }

    fun updateFood(food: FoodEntry) {
        viewModelScope.launch {
            _unitUiState.value = FoodUiState.Loading
            val result: DbResponse<Unit> = updateFoodEntryUseCase(food) // Assuming update returns DbResponse<Unit>
            _unitUiState.value = when (result) {
                is DbResponse.Success -> FoodUiState.Success(result.data) // result.data is Unit
                is DbResponse.Error -> FoodUiState.Error(result.error)
            }
        }
    }

    fun deleteFood(food: FoodEntry) {
        viewModelScope.launch {
            _unitUiState.value = FoodUiState.Loading
            val result: DbResponse<Unit> = deleteFoodEntryUseCase(food) // Assuming delete returns DbResponse<Unit>
            _unitUiState.value = when (result) {
                is DbResponse.Success -> FoodUiState.Success(result.data) // result.data is Unit
                is DbResponse.Error -> FoodUiState.Error(result.error)
            }
        }
    }

    fun dropAllFood() {
        viewModelScope.launch {
            _unitUiState.value = FoodUiState.Loading
            val result: DbResponse<Unit> = dropTableUseCase() // Assuming drop returns DbResponse<Unit>
            _unitUiState.value = when (result) {
                is DbResponse.Success -> FoodUiState.Success(result.data) // result.data is Unit
                is DbResponse.Error -> FoodUiState.Error(result.error)
            }
        }
    }

    fun getFoodById(id: Int) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result: DbResponse<FoodEntry?> = loadFoodByIdUseCase(id) // Assuming load by ID returns DbResponse<FoodEntry?>
            _foodUiState.value = when (result) {
                is DbResponse.Success -> FoodUiState.Success(result.data) as FoodUiState<FoodEntry>
                is DbResponse.Error -> FoodUiState.Error(result.error)
            }
        }
    }

    fun updateDoneField(done: Int, id: Int) {
        viewModelScope.launch {
            _unitUiState.value = FoodUiState.Loading
            val result: DbResponse<Unit> = updateDoneFieldUseCase(done, id) // Assuming update done returns DbResponse<Unit>
            _unitUiState.value = when (result) {
                is DbResponse.Success -> FoodUiState.Success(result.data)
                is DbResponse.Error -> FoodUiState.Error(result.error)
            }
        }
    }

    fun getAllFood() {
        viewModelScope.launch {
            _foodListUiState.value = FoodListUiState.Loading
            val result: DbResponse<List<FoodEntry>> = loadAllFoodUseCase() // Assuming load all returns DbResponse<List<FoodEntry>>
            _foodListUiState.value = when (result) {
                is DbResponse.Success -> FoodListUiState.Success(result.data)
                is DbResponse.Error -> FoodListUiState.Error(result.error)
            }
        }
    }

    fun getConsumedFood() {
        viewModelScope.launch {
            _foodListUiState.value = FoodListUiState.Loading
            // Assuming loadConsumedFoodUseCase returns DbResponse<List<FoodEntry>>
            val result: DbResponse<List<FoodEntry>> = loadConsumedFoodUseCase()
            _foodListUiState.value = when (result) {
                is DbResponse.Success -> FoodListUiState.Success(result.data) // result.data is List<FoodEntry>
                is DbResponse.Error -> FoodListUiState.Error(result.error)
            }
        }
    }


    fun getExpiredFood(referenceDate: Long) {
        viewModelScope.launch {
            _foodListUiState.value = FoodListUiState.Loading
            // Assuming loadExpiredFoodUseCase returns DbResponse<List<FoodEntry>>
            val result: DbResponse<List<FoodEntry>> = loadExpiredFoodUseCase(referenceDate)
            _foodListUiState.value = when (result) {
                is DbResponse.Success -> FoodListUiState.Success(result.data) // result.data is List<FoodEntry>
                is DbResponse.Error -> FoodListUiState.Error(result.error)
            }
        }
    }

    fun getExpiringFood(referenceDate: Long) {
        viewModelScope.launch {
            _foodListUiState.value = FoodListUiState.Loading
            // Assuming loadExpiringFoodUseCase returns DbResponse<List<FoodEntry>>
            val result: DbResponse<List<FoodEntry>> = loadExpiringFoodUseCase(referenceDate)
            _foodListUiState.value = when (result) {
                is DbResponse.Success -> FoodListUiState.Success(result.data) // result.data is List<FoodEntry>
                is DbResponse.Error -> FoodListUiState.Error(result.error)
            }
        }
    }

    fun getFoodExpiringToday(before: Long, after: Long) {
        viewModelScope.launch {
            _foodListUiState.value = FoodListUiState.Loading
            // Assuming loadFoodExpiringTodayUseCase returns DbResponse<List<FoodEntry>>
            val result: DbResponse<List<FoodEntry>> = loadFoodExpiringTodayUseCase(before, after)
            _foodListUiState.value = when (result) {
                is DbResponse.Success -> FoodListUiState.Success(result.data) // result.data is List<FoodEntry>
                is DbResponse.Error -> FoodListUiState.Error(result.error)
            }
        }
    }



    /*fun insertFood(food: FoodEntry) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = insertFoodEntryUseCase(food)
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun updateFood(food: FoodEntry) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = updateFoodEntryUseCase(food)
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun deleteFood(food: FoodEntry) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = deleteFoodEntryUseCase(food)
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun dropAllFood() {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = dropTableUseCase()
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun getFoodById(id: Int) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = loadFoodByIdUseCase(id)
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun updateDoneField(done: Int, id: Int) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = updateDoneFieldUseCase(done, id)
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun getAllFood() {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = loadAllFoodUseCase()
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun getConsumedFood() {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = loadConsumedFoodUseCase()
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun getExpiredFood(referenceDate: Long) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = loadExpiredFoodUseCase(referenceDate)
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun getExpiringFood(referenceDate: Long) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = loadExpiringFoodUseCase(referenceDate)
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun getFoodExpiringToday(before: Long, after: Long) {
        viewModelScope.launch {
            _foodUiState.value = FoodUiState.Loading
            val result = loadFoodExpiringTodayUseCase(before, after)
            _foodUiState.value = handleDbResult(result)
        }
    }

    fun <T> handleDbResult(result: DbResponse<T>): FoodUiState<T> {
        return when (result) {
            is DbResponse.Success -> FoodUiState.Success(result.data)
            is DbResponse.Error -> FoodUiState.Error(result.error)
        }
    }*/
}
