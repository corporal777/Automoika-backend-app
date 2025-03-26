package kg.automoika.data.remote

import kg.automoika.data.body.CarWashBody
import kg.automoika.data.response.CarWashShortLocationModel
import kg.automoika.data.response.CarWashShortResponse
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class CarWashRemote(
    @BsonId
    val id: String,
    val name: String,
    val description: String,
    val createdAt: String,
    val backgroundImage: CarWashImageModel,
    val images: List<CarWashImageModel>,
    val address: CarWashLocationModel,
    val contacts: CarWashContactsModel,
    var boxes: CarWashBoxesModel,
    val favourites: List<String>,
    val type: String,
    val owner: String
)

@Serializable
data class CarWashLocationModel(
    val street: String,
    val district: String,
    val city: String,
    val lat: String,
    val lon: String,
    val wayDescription: String
)

@Serializable
data class CarWashImageModel(
    val imageName: String,
    val imageUrl: String
)

@Serializable
data class CarWashContactsModel(
    val phone: String,
    val whatsapp: String,
    val instagram: String
)

@Serializable
data class CarWashBoxesModel(
    var count: String,
    var free: String,
)



