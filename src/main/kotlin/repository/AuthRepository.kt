package kg.automoika.repository

import io.ktor.server.application.*
import kg.automoika.data.body.CodeBody
import kg.automoika.data.body.LoginBody
import kg.automoika.data.body.PhoneBody
import kg.automoika.data.remote.UserRemote
import kg.automoika.data.response.TokenResponse
import kg.automoika.data.response.UserResponse

interface AuthRepository {

    suspend fun checkAuth(call: ApplicationCall) : Boolean
    suspend fun createToken() : TokenResponse?
    suspend fun checkToken(token : String) : Boolean

    suspend fun login(body : LoginBody, call: ApplicationCall) : UserResponse?

    suspend fun sendCode(body : PhoneBody) : Boolean
    suspend fun confirmCode(body : CodeBody) : Boolean

    suspend fun checkPhoneExists(phone : String) : String?
}