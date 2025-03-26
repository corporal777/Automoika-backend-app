package kg.automoika.data.body

import kotlinx.serialization.Serializable

@Serializable
data class ReviewBody(
    val userId : String,
    val carWashId : String,
    val reviewText : String
)