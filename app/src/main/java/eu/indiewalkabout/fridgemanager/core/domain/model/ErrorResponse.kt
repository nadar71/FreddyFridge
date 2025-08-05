package eu.indiewalkabout.fridgemanager.core.domain.model

data class ErrorResponse(
    val code: Int,
    val messages: List<String>,
    val error: String
)