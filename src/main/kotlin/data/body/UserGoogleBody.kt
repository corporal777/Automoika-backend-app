package kg.automoika.data.body

import kg.automoika.data.remote.*
import kg.automoika.extensions.TYPE_USER
import kotlinx.serialization.Serializable

@Serializable
data class UserGoogleBody(
    val id : String,
    val name: String,
    val lastName: String?
) {
    fun createRemote(): UserRemote {
        return UserRemote(
            id = id,
            name = name,
            createdAt = System.currentTimeMillis().toString(),
            image = CarWashImageModel("",""),
            login = UserLoginModel("-", false),
            password = UserPasswordModel("-", true),
            account = UserAccountModel(TYPE_USER, listOf("")),
            state = UserStateModel("", false)
        )
    }
}