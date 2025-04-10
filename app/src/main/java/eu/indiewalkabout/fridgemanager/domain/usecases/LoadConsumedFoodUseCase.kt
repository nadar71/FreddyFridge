package eu.indiewalkabout.fridgemanager.domain.usecases

import eu.indiewalkabout.fridgemanager.core.domain.model.ApiResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import javax.inject.Inject

class LoadConsumedFoodUseCase @Inject constructor(
    private val repository: FridgeManagerRepository
) {
    suspend operator fun invoke(): ApiResponse<List<FoodEntry>> {
        return try {
            val result = repository.loadAllFoodSaved_no_livedata()
            ApiResponse.Success(result)
        } catch (e: Exception) {
            ApiResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?: "Unknown error"))
        }
    }
}