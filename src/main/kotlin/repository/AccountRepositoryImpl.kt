package kg.automoika.repository

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kg.automoika.data.body.AccountBody
import kg.automoika.data.body.LoginBody
import kg.automoika.data.remote.AccountRemote
import kg.automoika.data.response.ErrorResponse
import kg.automoika.data.response.LoginResponse
import kg.automoika.data.response.TokenResponse
import kg.automoika.extensions.ACCOUNTS_COLLECTION
import kg.automoika.utils.findAccByLogin
import kotlinx.coroutines.flow.firstOrNull

class AccountRepositoryImpl(private val database: MongoDatabase) : AccountRepository {

    private val accCollection get() = database.getCollection<AccountRemote>(ACCOUNTS_COLLECTION)

    override suspend fun loginAccount(body: LoginBody, call: ApplicationCall): LoginResponse? {
        val account = accCollection.findAccByLogin(body.login).firstOrNull()
        if (account == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found"))
            return null
        } else if (account.password.value != body.password){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Password is not valid"))
            return null
        } else return account.toResponse()
    }

    override suspend fun createAccount(body: AccountBody): LoginResponse? {
        val remote = body.createRemote()
        val result = accCollection.insertOne(remote)
        return if (result.wasAcknowledged()) remote.toResponse() else null
    }
}