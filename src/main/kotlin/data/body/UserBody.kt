package kg.automoika.data.body

import io.ktor.http.content.*
import kg.automoika.data.remote.*
import kg.automoika.extensions.*
import kg.automoika.extensions.FileUtils.saveImageLocal
import kg.automoika.extensions.FileUtils.uploadImageToFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class UserBody(
    val id : String,
    var name: String = "",
    var lastName: String = "",
    var phone : String = "",
    var password : String = "",
    var image: CarWashImageModel? = null,
){

    fun setData(partData: PartData.FormItem) {
        when (partData.name) {
            "name" -> name = partData.value
            "lastName" -> lastName = partData.value
            "phone" -> phone = partData.value
            "password" -> password = partData.value
        }
    }

    suspend fun setImage(partData: PartData.FileItem) {
        val fileName = "user-$id"
        val fileUrl = uploadImageToFirebase(partData, fileName)
        image = CarWashImageModel(fileName, fileUrl)
    }

    fun createRemote(): UserRemote {
        return UserRemote(
            id = id,
            name = name,
            lastName = lastName,
            createdAt = System.currentTimeMillis().toString(),
            image = image ?: CarWashImageModel("",""),
            login = UserLoginModel(phone, true),
            password = UserPasswordModel(password, false),
            account = UserAccountModel(TYPE_USER, listOf("")),
            state = UserStateModel("", false)
        )
    }
}