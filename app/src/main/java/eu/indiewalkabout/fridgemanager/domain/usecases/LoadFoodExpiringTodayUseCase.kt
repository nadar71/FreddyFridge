package eu.indiewalkabout.fridgemanager.domain.usecases

import eu.indiewalkabout.fridgemanager.core.domain.model.ApiResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.domain.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import javax.inject.Inject

class LoadFoodExpiringTodayUseCase @Inject constructor(
    private val repository: FridgeManagerRepository
) {
    suspend operator fun invoke(dayBefore: Long?, dayAfter: Long?): ApiResponse<List<FoodEntry>> {
        return try {
            val result = repository.loadFoodExpiringToday_no_livedata(dayBefore, dayAfter)
            ApiResponse.Success(result)
        } catch (e: Exception) {
            ApiResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?: "Unknown error"))
        }
    }
}