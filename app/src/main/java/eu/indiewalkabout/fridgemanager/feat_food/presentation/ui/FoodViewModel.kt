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
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _uiState = MutableStateFlow<FoodUiState<Any>>(FoodUiState.Idle)
    val uiState: StateFlow<FoodUiState<Any>> = _uiState

    fun insertFood(food: FoodEntry) {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = insertFoodEntryUseCase(food)
            _uiState.value = handleDbResult(result)
        }
    }

    fun updateFood(food: FoodEntry) {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = updateFoodEntryUseCase(food)
            _uiState.value = handleDbResult(result)
        }
    }

    fun deleteFood(food: FoodEntry) {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = deleteFoodEntryUseCase(food)
            _uiState.value = handleDbResult(result)
        }
    }

    fun dropAllFood() {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = dropTableUseCase()
            _uiState.value = handleDbResult(result)
        }
    }

    fun getFoodById(id: Int) {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = loadFoodByIdUseCase(id)
            _uiState.value = handleDbResult(result)
        }
    }

    fun updateDoneField(done: Int, id: Int) {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = updateDoneFieldUseCase(done, id)
            _uiState.value = handleDbResult(result)
        }
    }

    fun getAllFood() {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = loadAllFoodUseCase()
            _uiState.value = handleDbResult(result)
        }
    }

    fun getConsumedFood() {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = loadConsumedFoodUseCase()
            _uiState.value = handleDbResult(result)
        }
    }

    fun getExpiredFood(referenceDate: Long) {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = loadExpiredFoodUseCase(referenceDate)
            _uiState.value = handleDbResult(result)
        }
    }

    fun getExpiringFood(referenceDate: Long) {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = loadExpiringFoodUseCase(referenceDate)
            _uiState.value = handleDbResult(result)
        }
    }

    fun getFoodExpiringToday(before: Long, after: Long) {
        viewModelScope.launch {
            _uiState.value = FoodUiState.Loading
            val result = loadFoodExpiringTodayUseCase(before, after)
            _uiState.value = handleDbResult(result)
        }
    }

    fun <T> handleDbResult(result: DbResponse<T>): FoodUiState<T> {
        return when (result) {
            is DbResponse.Success -> FoodUiState.Success(result.data)
            is DbResponse.Error -> FoodUiState.Error(result.error)
        }
    }
}
