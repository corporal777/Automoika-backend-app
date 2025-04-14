package kg.automoika.data.response

import kg.automoika.data.remote.*
import kotlinx.serialization.Serializable

@Serializable
data class CarWashFullResponse(
    val id: String,
    val name: String,
    val description: String,
    val createdAt : String,
    val backgroundImage: CarWashImageModel,
    val images: List<CarWashImageModel>,
    val address: CarWashLocationModel,
    val contacts: CarWashContactsModel,
    var boxes: CarWashBoxesModel,
    val favourites: List<String>,
    val type: String,
    val owner: String,
    val binds : CarWashBinds
) {
    companion object {
        fun fromRemote(remote: CarWashRemote): CarWashFullResponse {
            return CarWashFullResponse(
                id = remote.id,
                name = remote.name,
                description = remote.description,
                createdAt = remote.createdAt,
                backgroundImage = remote.backgroundImage,
                images = remote.images,
                address = remote.address,
                contacts = remote.contacts,
                boxes = remote.boxes,
                owner = remote.owner,
                favourites = remote.favourites,
                type = remote.type,
                binds = CarWashBinds()
            )
        }
    }
}

@Serializable
data class CarWashBinds(
    var review : List<ReviewResponse> = emptyList()
)