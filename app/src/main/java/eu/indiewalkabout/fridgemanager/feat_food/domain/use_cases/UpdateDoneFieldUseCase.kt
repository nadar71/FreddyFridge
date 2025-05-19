package eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases

import eu.indiewalkabout.fridgemanager.core.domain.model.DbResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import javax.inject.Inject

class UpdateDoneFieldUseCase @Inject constructor(
    private val repository: FridgeManagerRepository
) {
    operator fun invoke(done: Int, id: Int): DbResponse<Unit> {
        return try {
            repository.updateDoneField(done, id)
            DbResponse.Success(Unit)
        } catch (e: Exception) {
            DbResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?: "Unknown error"))
        }
    }
}