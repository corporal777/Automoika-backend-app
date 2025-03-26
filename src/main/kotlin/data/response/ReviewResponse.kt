package kg.automoika.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponse(
    val name : String? = null,
    val lastName : String? = null,
    val image : String? = null,
    val reviewText : String
)