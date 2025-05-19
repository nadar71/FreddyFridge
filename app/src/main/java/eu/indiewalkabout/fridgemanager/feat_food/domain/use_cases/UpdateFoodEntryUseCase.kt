package eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import javax.inject.Inject

class UpdateFoodEntryUseCase @Inject constructor(
    private val repository: FridgeManagerRepository,
    @ApplicationContext context: Context
) {
    private val context = context
    operator fun invoke(foodEntry: FoodEntry): DbResponse<Unit> {
        return try {
            repository.updateFoodEntry(foodEntry)
            DbResponse.Success(Unit)
        } catch (e: Exception) {
            Log.e("UpdateFoodEntryUseCase", e.localizedMessage ?:
            context.getString(R.string.db_error_updating_food_entry))

            DbResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?:
            context.getString(R.string.db_error_updating_food_entry)))
        }
    }
}