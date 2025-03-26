package kg.automoika.data.response

import kotlinx.serialization.Serializable

@Serializable
class YandexLocationResponse(
    val fullAddress : String?,
    val country : String?,
    val city : String?,
    val street : String?,
    val district : String?,
)