package kg.automoika.data.body

import kg.automoika.data.remote.*
import kg.automoika.extensions.*
import kotlinx.serialization.Serializable

@Serializable
data class UserBody(
    val id : String?,
    val name: String,
    val phone : String,
    val password : String,

){

//    fun setData(partData: PartData.FormItem) {
//        when (partData.name) {
//            "name" -> name = partData.value
//            "phone" -> phone = partData.value
//            "password" -> password = partData.value
//        }
//    }
//
//    suspend fun setImage(partData: PartData.FileItem) {
//        val fileName = "user-$id"
//        val fileUrl = uploadImageToFirebase(partData, fileName)
//        image = CarWashImageModel(fileName, fileUrl)
//    }

    fun createRemote(): UserRemote {
        val userId = if (id.isNullOrEmpty()) generateId() else id
        val login = UserLoginModel(phone, phone != "-")
        val password = UserPasswordModel(password, password == "-")
        return UserRemote(
            id = userId,
            name = name,
            createdAt = System.currentTimeMillis().toString(),
            image = CarWashImageModel("",""),
            login = login,
            password = password,
            account = UserAccountModel(TYPE_USER, listOf("")),
            state = UserStateModel("", false)
        )
    }
}