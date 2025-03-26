package kg.automoika.repository

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kg.automoika.data.body.CodeBody
import kg.automoika.data.body.LoginBody
import kg.automoika.data.body.PhoneBody
import kg.automoika.data.remote.UserRemote
import kg.automoika.data.response.ErrorResponse
import kg.automoika.data.response.TokenResponse
import kg.automoika.data.response.UserResponse
import kg.automoika.db.AuthDatabase
import kg.automoika.extensions.ACCOUNTS_COLLECTION
import kg.automoika.extensions.NotificationUtils
import kg.automoika.extensions.USERS_COLLECTION
import kg.automoika.extensions.daysBetween
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class AuthRepositoryImpl(
    private val localDb: AuthDatabase,
    private val remoteDb: MongoDatabase
) : AuthRepository {

    private val usersCollection get() = remoteDb.getCollection<UserRemote>(USERS_COLLECTION)

    override suspend fun checkAuth(call: ApplicationCall): Boolean {
        val authHeader = call.request.header("Authorization")
        val hasToken = if (authHeader.isNullOrEmpty() || authHeader.length < 15) false
        else localDb.getToken(authHeader.replace("Token", "").replace(" ", "")) != null
        //if (!hasToken) call.respond(HttpStatusCode.Unauthorized)
        //return hasToken
        return true
    }

    override suspend fun checkToken(token: String): Boolean {
        val local = localDb.getToken(token)
        return if (local == null) false
        else daysBetween(local.createdAt.toLong(), System.currentTimeMillis()) < 30
    }

    override suspend fun createToken(): TokenResponse? {
        return localDb.createToken()
    }


    override suspend fun login(body: LoginBody, call: ApplicationCall): UserResponse? {
        val query = Filters.eq("login.phone", body.login)
        val user = usersCollection.find<UserRemote>(query).firstOrNull()
        if (user == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found"))
            return null
        } else if (user.password.value != body.password){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Password is not valid"))
            return null
        } else return user.toResponse()
    }

    override suspend fun sendCode(body: PhoneBody): Boolean {
        val code = localDb.createCode(body.phone)
        val result = NotificationUtils.sendVerificationCode(body.fcmToken, code)
        return !result.isNullOrEmpty()
    }

    override suspend fun confirmCode(body: CodeBody): Boolean {
        return localDb.checkCode(body.code, body.phone)
    }


    override suspend fun checkPhoneExists(phone: String): String? {
        val users = remoteDb.getCollection<UserRemote>(USERS_COLLECTION).find().toList()
        return users.find { x -> x.login.phone == phone }?.id
    }
}