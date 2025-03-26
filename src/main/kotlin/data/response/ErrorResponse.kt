package kg.automoika.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String
)