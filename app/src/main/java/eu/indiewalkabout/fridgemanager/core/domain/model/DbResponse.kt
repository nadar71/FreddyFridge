package eu.indiewalkabout.fridgemanager.core.domain.model

sealed class DbResponse<out T> {
    data class Success<out T>(val data: T) : DbResponse<T>()
    data class Error(val error: ErrorResponse) : DbResponse<Nothing>()
}

