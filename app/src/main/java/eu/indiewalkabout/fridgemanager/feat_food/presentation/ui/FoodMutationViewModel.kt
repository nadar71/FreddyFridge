package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.DeleteFoodEntryUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.UpdateFoodEntryUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FoodMutationEvent {
    data object Success : FoodMutationEvent
    data class Error(val error: ErrorResponse) : FoodMutationEvent
}

@HiltViewModel
class FoodMutationViewModel @Inject constructor(
    private val updateFoodEntryUseCase: UpdateFoodEntryUseCase,
    private val deleteFoodEntryUseCase: DeleteFoodEntryUseCase,
) : ViewModel() {

    private val tag = "FoodMutationViewModel"

    private val _isMutating = MutableStateFlow(false)
    val isMutating: StateFlow<Boolean> = _isMutating.asStateFlow()
    private val _events = MutableSharedFlow<FoodMutationEvent>()
    val events: SharedFlow<FoodMutationEvent> = _events.asSharedFlow()

    fun updateFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _isMutating.value = true
            try {
                val result: DbResponse<Unit> = updateFoodEntryUseCase(foodEntry)
                Log.d(tag, "updateFoodEntry: $result from db operation")
                when (result) {
                    is DbResponse.Success -> _events.emit(FoodMutationEvent.Success)
                    is DbResponse.Error -> _events.emit(FoodMutationEvent.Error(result.error))
                }
            } catch (e: Exception) {
                _events.emit(
                    FoodMutationEvent.Error(
                        ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                    )
                )
            } finally {
                _isMutating.value = false
            }
        }
    }

    fun deleteFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            _isMutating.value = true
            try {
                val result: DbResponse<Unit> = deleteFoodEntryUseCase(foodEntry)
                when (result) {
                    is DbResponse.Success -> _events.emit(FoodMutationEvent.Success)
                    is DbResponse.Error -> _events.emit(FoodMutationEvent.Error(result.error))
                }
            } catch (e: Exception) {
                _events.emit(
                    FoodMutationEvent.Error(
                        ErrorResponse(0, emptyList(), e.message ?: "Unknown error")
                    )
                )
            } finally {
                _isMutating.value = false
            }
        }
    }
}
