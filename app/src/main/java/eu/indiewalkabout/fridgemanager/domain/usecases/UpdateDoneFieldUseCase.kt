package eu.indiewalkabout.fridgemanager.domain.usecases

import eu.indiewalkabout.fridgemanager.core.domain.model.ApiResponse
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.domain.repository.FridgeManagerRepository
import javax.inject.Inject

class UpdateDoneFieldUseCase @Inject constructor(
    private val repository: FridgeManagerRepository
) {
    operator fun invoke(done: Int, id: Int): ApiResponse<Unit> {
        return try {
            repository.updateDoneField(done, id)
            ApiResponse.Success(Unit)
        } catch (e: Exception) {
            ApiResponse.Error(ErrorResponse(0, listOf(), e.localizedMessage ?: "Unknown error"))
        }
    }
}