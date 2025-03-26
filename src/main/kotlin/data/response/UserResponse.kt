package kg.automoika.data.response

import kg.automoika.data.remote.*
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val name: String,
    val image: CarWashImageModel,
    val login : UserLoginModel,
    val account : UserAccountModel,
)