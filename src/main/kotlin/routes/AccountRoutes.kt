package kg.automoika.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.automoika.data.body.AccountBody
import kg.automoika.data.body.LoginBody
import kg.automoika.data.body.TokenBody
import kg.automoika.data.body.UserBody
import kg.automoika.repository.AccountRepository
import kg.automoika.repository.AuthRepository
import org.koin.ktor.ext.inject

fun Route.accountRoutes() {
    val auth by inject<AuthRepository>()
    val repository by inject<AccountRepository>()

    post("v1/create-account") {
        if (!auth.checkAuth(call)) return@post

        val request = call.receive<AccountBody>()
        val response = repository.createAccount(request)
        if (response != null) call.respond(HttpStatusCode.Created, response)
        else call.respond(HttpStatusCode.BadRequest)
    }

    post("v1/login-account"){
        if (!auth.checkAuth(call)) return@post

        val request = call.receive<LoginBody>()
        val response = repository.loginAccount(request, call) ?: return@post
        call.respond(response)
    }
}