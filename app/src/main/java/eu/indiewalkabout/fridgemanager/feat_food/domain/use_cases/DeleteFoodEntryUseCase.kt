package eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases

import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import javax.inject.Inject

class DeleteFoodEntryUseCase @Inject constructor(
    private val repository: FridgeManagerRepository
) {
    operator fun invoke(foodEntry: FoodEntry): DbResponse<Unit> {
        return try {
            repository.deleteFoodEntry(foodEntry)
            DbResponse.Success(Unit)
        } catch (e: Exception) {
            DbResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?: "Unknown error"))
        }
    }
}