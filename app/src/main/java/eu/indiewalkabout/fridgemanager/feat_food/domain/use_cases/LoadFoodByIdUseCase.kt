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

class LoadFoodByIdUseCase @Inject constructor(
    private val repository: FridgeManagerRepository,
    @ApplicationContext context: Context
) {
    private val context = context
    suspend operator fun invoke(id: Int): DbResponse<FoodEntry> {
        return try {
            val result = repository.loadFoodById_no_livedata(id)
            DbResponse.Success(result)
        } catch (e: Exception) {
            Log.e("LoadFoodByIdUseCase", e.localizedMessage ?:
            context.getString(R.string.db_error_loading_food))

            DbResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?:
            context.getString(R.string.db_error_loading_food)))
        }
    }
}