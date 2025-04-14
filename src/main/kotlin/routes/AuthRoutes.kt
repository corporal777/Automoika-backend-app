package kg.automoika.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kg.automoika.data.body.*
import kg.automoika.repository.AuthRepository
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val repository by inject<AuthRepository>()

    get("v1/get-token") {
        val token = repository.createToken()
        if (token == null) call.respond(HttpStatusCode.Conflict)
        else call.respond(HttpStatusCode.Created, token)
    }

    post("v1/check-token") {
        val request = call.receive<TokenBody>()
        val isNotExpired = repository.checkToken(request.token)
        if (isNotExpired) call.respond(HttpStatusCode.OK)
        else call.respond(HttpStatusCode.Forbidden)
    }


    post("v1/check-phone-exists"){
        if (!repository.checkAuth(call)) return@post
        val request = call.receive<PhoneBody>()
        val userId = repository.checkPhoneExists(request.phone)
        call.respond(userId ?: "-1")
    }

    post("v1/send-code"){
        if (!repository.checkAuth(call)) return@post
        val codeSent = repository.sendCode(call.receive<PhoneBody>())
        call.respond(if (codeSent) HttpStatusCode.OK else HttpStatusCode.NotFound)
    }

    post("v1/confirm-code"){
        if (!repository.checkAuth(call)) return@post
        val codeSuccess = repository.confirmCode(call.receive<CodeBody>())
        call.respond(if (codeSuccess) HttpStatusCode.OK else HttpStatusCode.NotFound)
    }
}