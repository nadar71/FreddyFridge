package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.InsertFoodEntryUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface InsertFoodEvent {
    data object Success : InsertFoodEvent
    data class Error(val error: ErrorResponse) : InsertFoodEvent
}

@HiltViewModel
class InsertFoodViewModel @Inject constructor(
    private val insertFoodEntryUseCase: InsertFoodEntryUseCase,
) : ViewModel() {

    private val _isInserting = MutableStateFlow(false)
    val isInserting: StateFlow<Boolean> = _isInserting.asStateFlow()
    private val _events = MutableSharedFlow<InsertFoodEvent>()
    val events: SharedFlow<InsertFoodEvent> = _events.asSharedFlow()

    fun insertFood(foodEntry: FoodEntry) {
        insertFoods(listOf(foodEntry))
    }

    fun insertFoods(foodEntries: List<FoodEntry>) {
        viewModelScope.launch {
            _isInserting.value = true
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

                if (failure != null) {
                    _events.emit(InsertFoodEvent.Error(failure.error))
                } else {
                    _events.emit(InsertFoodEvent.Success)
                }
            } catch (e: Exception) {
                _events.emit(
                    InsertFoodEvent.Error(
                        ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                    )
                )
            } finally {
                _isInserting.value = false
            }
        }
    }
}
