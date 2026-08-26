package eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import javax.inject.Inject

class LoadFoodExpiringForNotificationUseCase @Inject constructor(
    private val repository: FridgeManagerRepository,
) {
    suspend operator fun invoke(cutoffDate: Long?): List<FoodEntry> {
        return repository.loadAllFoodExpiring(cutoffDate)
    }
}
