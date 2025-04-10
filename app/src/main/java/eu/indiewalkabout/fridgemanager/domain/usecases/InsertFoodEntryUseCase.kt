package eu.indiewalkabout.fridgemanager.domain.usecases

import eu.indiewalkabout.fridgemanager.core.domain.model.ApiResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import javax.inject.Inject

class InsertFoodEntryUseCase @Inject constructor(
    private val repository: FridgeManagerRepository
) {
    operator fun invoke(foodEntry: FoodEntry): ApiResponse<Unit> {
        return try {
            repository.insertFoodEntry(foodEntry)
            ApiResponse.Success(Unit)
        } catch (e: Exception) {
            ApiResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?: "Unknown error"))
        }
    }
}