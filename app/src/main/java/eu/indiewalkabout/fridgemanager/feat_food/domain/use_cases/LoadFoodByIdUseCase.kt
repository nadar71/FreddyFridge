package eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases

import eu.indiewalkabout.fridgemanager.core.domain.model.ApiResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import javax.inject.Inject

class LoadFoodByIdUseCase @Inject constructor(
    private val repository: FridgeManagerRepository
) {
    suspend operator fun invoke(id: Int): ApiResponse<FoodEntry> {
        return try {
            val result = repository.loadFoodById_no_livedata(id)
            ApiResponse.Success(result)
        } catch (e: Exception) {
            ApiResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?: "Unknown error"))
        }
    }
}