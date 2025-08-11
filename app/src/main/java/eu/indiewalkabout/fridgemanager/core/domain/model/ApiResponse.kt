package eu.indiewalkabout.fridgemanager.core.domain.model

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val error: ErrorResponse) : ApiResponse<Nothing>()
}

