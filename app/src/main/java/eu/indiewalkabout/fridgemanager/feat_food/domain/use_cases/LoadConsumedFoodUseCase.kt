package eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases

import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import javax.inject.Inject

class LoadConsumedFoodUseCase @Inject constructor(
    private val repository: FridgeManagerRepository
) {
    suspend operator fun invoke(): DbResponse<List<FoodEntry>> {
        return try {
            val result = repository.loadAllFoodSaved_no_livedata()
            DbResponse.Success(result)
        } catch (e: Exception) {
            DbResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?: "Unknown error"))
        }
    }
}