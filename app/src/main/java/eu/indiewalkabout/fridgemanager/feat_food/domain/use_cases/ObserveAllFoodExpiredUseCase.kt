package eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveAllFoodExpiredUseCase @Inject constructor(
    private val repository: FridgeManagerRepository,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(date: Long?): Flow<DbResponse<List<FoodEntry>>> {
        return repository.observeAllFoodExpired(date)
            .map<List<FoodEntry>, DbResponse<List<FoodEntry>>> { DbResponse.Success(it) }
            .catch { e ->
                Log.e(
                    "LoadExpiredFoodUseCase",
                    e.localizedMessage ?: context.getString(R.string.db_error_loading_expired_food)
                )
                emit(
                    DbResponse.Error(
                        ErrorResponse(
                            0,
                            listOf(),
                            e.localizedMessage ?: context.getString(R.string.db_error_loading_expired_food)
                        )
                    )
                )
            }
    }
}
