package kg.automoika.data.request

import kotlinx.serialization.Serializable

@Serializable
data class LocationRequest(
    val lat: String,
    val lon: String
)