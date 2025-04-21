package kg.automoika.data.body

import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    val login : String,
    val password : String,
)

@Serializable
data class TokenBody(
    val token : String,
)

@Serializable
data class PhoneCheckBody(
    val phone : String
)

@Serializable
data class PhoneBody(
    val phone : String,
    val fcmToken : String
)

@Serializable
data class CodeBody(
    val phone : String,
    val code : String,
)