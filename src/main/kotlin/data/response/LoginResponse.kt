package kg.automoika.data.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val id: String,
    val login : String
)