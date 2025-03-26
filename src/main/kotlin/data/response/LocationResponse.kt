package kg.automoika.data.response

import kg.automoika.data.request.LocationRequest
import kotlinx.serialization.Serializable


@Serializable
data class LocationDataResponse(
    val data: List<LocationResponse>,
)

@Serializable
data class LocationResponse(
    val lat: String,
    val lon: String,
    val city: String?,
)