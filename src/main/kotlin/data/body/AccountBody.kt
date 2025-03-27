package kg.automoika.data.body

import kg.automoika.data.remote.*
import kg.automoika.extensions.TYPE_USER
import kg.automoika.extensions.generateId
import kotlinx.serialization.Serializable

@Serializable
data class AccountBody(
    val login : String,
    val password : String,
    val type : String
) {
    fun createRemote(): AccountRemote {
        return AccountRemote(
            id = generateId(),
            createdAt = System.currentTimeMillis().toString(),
            type = type,
            login = UserLoginModel(login, true),
            password = UserPasswordModel(password, false),
            carWash = emptyList()
        )
    }
}