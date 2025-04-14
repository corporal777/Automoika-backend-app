package kg.automoika.data.remote

import kg.automoika.data.body.CarWashBody
import kg.automoika.data.body.UserBody
import kg.automoika.data.body.UserGoogleBody
import kg.automoika.data.response.UserResponse
import kg.automoika.extensions.TYPE_CAR_WASH_OWNER
import kg.automoika.extensions.TYPE_USER
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class UserRemote(
    @BsonId
    val id: String,
    val name: String,
    val createdAt: String,
    val image: CarWashImageModel,
    val login : UserLoginModel,
    val password : UserPasswordModel,
    val state : UserStateModel
) {
    fun toResponse() : UserResponse = UserResponse(id, name, createdAt, image, password.isAbsent, login)
}

@Serializable
data class UserLoginModel(
    val phone : String,
    val isConfirmed : Boolean,
)

@Serializable
data class UserPasswordModel(
    val value : String,
    val isAbsent : Boolean,
)

@Serializable
data class UserAccountModel(
    var type : String,
    var carWash : List<String>,
)

@Serializable
data class UserStateModel(
    val fcmToken : String,
    val loggedOut : Boolean,
)