package kg.automoika.data.remote

import kg.automoika.data.response.LoginResponse
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class AccountRemote(
    @BsonId
    val id : String,
    val type : String,
    val createdAt: String,
    val login : UserLoginModel,
    val password : UserPasswordModel,
    val carWash :  List<String>,
) {
    fun toResponse() = LoginResponse(id, login.phone)
}