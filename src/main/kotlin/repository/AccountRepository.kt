package kg.automoika.repository

import io.ktor.server.application.*
import kg.automoika.data.body.AccountBody
import kg.automoika.data.body.LoginBody
import kg.automoika.data.response.LoginResponse

interface AccountRepository {
    suspend fun loginAccount(body : LoginBody, call: ApplicationCall) : LoginResponse?
    suspend fun createAccount(body: AccountBody) : LoginResponse?
}