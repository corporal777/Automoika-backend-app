package kg.automoika.data.body

import io.ktor.http.content.*
import kg.automoika.data.remote.*

data class CarWashBody(
    val id: String,
    var name: String = "",
    var description: String = "",

    var city: String = "",
    var street: String = "",
    var district: String = "",
    var lat: String = "",
    var lon: String = "",
    var wayDescription: String = "",

    var backgroundImage: CarWashImageModel? = null,
    var boxesCount: String = "0",

    var userId: String = "",

    var phone: String = "",
    var whatsapp: String = "",
    var instagram: String = "",

    var type: String = ""
) {
    companion object {
        fun CarWashBody.setData(partData: PartData.FormItem) {
            when (partData.name) {
                "name" -> name = partData.value
                "description" -> description = partData.value

                "city" -> city = partData.value
                "street" -> street = partData.value
                "district" -> district = partData.value
                "lat" -> lat = partData.value
                "lon" -> lon = partData.value
                "wayDescription" -> wayDescription = partData.value

                "boxes" -> boxesCount = partData.value

                "phone" -> phone = partData.value
                "whatsapp" -> whatsapp = partData.value
                "instagram" -> instagram = partData.value

                "type" -> type = partData.value

                "user" -> userId = partData.value
            }
        }

        fun CarWashBody.setBackgroundImage(fileName: String, fileUrl: String) {
            backgroundImage = CarWashImageModel(fileName, fileUrl)
        }
    }

    fun createRemote(imagesList: List<CarWashImageModel>): CarWashRemote {
        return CarWashRemote(
            id = id,
            name = name,
            description = description,
            createdAt = System.currentTimeMillis().toString(),
            backgroundImage = backgroundImage ?: CarWashImageModel("", ""),
            images = imagesList,
            address = CarWashLocationModel(
                street = street,
                district = district,
                city = city,
                lat = lat,
                lon = lon,
                wayDescription = wayDescription
            ),

            boxes = CarWashBoxesModel(boxesCount, "0"),
            contacts = CarWashContactsModel(phone, whatsapp, instagram),

            favourites = listOf(""),
            type = type,
            owner = userId
        )
    }
}

