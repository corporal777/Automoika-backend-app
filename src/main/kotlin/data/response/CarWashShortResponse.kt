package kg.automoika.data.response

import kg.automoika.data.remote.CarWashBoxesModel
import kg.automoika.data.remote.CarWashLocationModel
import kg.automoika.data.remote.CarWashRemote
import kotlinx.serialization.Serializable

@Serializable
data class CarWashShortResponse(
    val id: String,
    val name: String,
    val backgroundImage: String,
    val address : CarWashShortLocationModel,
    val boxes: CarWashBoxesModel,
    val owner : String,
    val type : String,
    val favourites : List<String>
) {
    companion object {
        fun fromRemote(remote: CarWashRemote): CarWashShortResponse {
            return CarWashShortResponse(
                id = remote.id,
                name = remote.name,
                backgroundImage = remote.backgroundImage.imageUrl,
                address = CarWashShortLocationModel(
                    street = remote.address.street,
                    district = remote.address.district,
                    city = remote.address.city,
                    lat = remote.address.lat,
                    lon = remote.address.lon
                ),
                boxes = remote.boxes,
                owner = remote.owner,
                favourites = remote.favourites,
                type = remote.type
            )
        }
    }
}

@Serializable
data class CarWashShortLocationModel(
    val street: String,
    val district: String,
    val city: String,
    val lat: String,
    val lon: String
)