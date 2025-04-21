package kg.automoika.routes

import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.automoika.data.body.CodeBody
import kg.automoika.data.body.PhoneBody
import kg.automoika.data.body.PhoneCheckBody
import kg.automoika.data.body.TokenBody
import kg.automoika.data.response.TokenResponse
import kg.automoika.extensions.body
import kg.automoika.extensions.successResponse
import kg.automoika.extensions.tag
import kg.automoika.repository.AuthRepository
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val repository by inject<AuthRepository>()

    get("v1/get-token", {
        tag = "auth"
        description = "Route for get authentication token"
        response { body<TokenResponse>(HttpStatusCode.Created) }
    }) {
        val token = repository.createToken()
        if (token == null) call.respond(HttpStatusCode.Conflict)
        else call.respond(HttpStatusCode.Created, token)
    }

    get("v1/check-token", {
        tag = "auth"
        description = "Route for check validation of token"
        request { body<TokenBody> { required = true } }
        response { code(HttpStatusCode.OK) {} }
    }) {
        val request = call.receive<TokenBody>()
        val isNotExpired = repository.checkToken(request.token)
        call.respond(if (isNotExpired) HttpStatusCode.OK else HttpStatusCode.Forbidden)
    }

    post("v1/check-phone-exists", {
        tag = "auth"
        protected = true
        description = "Route for check phone exists"
        request { body<PhoneCheckBody> { required = true } }
        response { body<String>(HttpStatusCode.OK) }
    }) {
        if (!repository.checkAuth(call)) return@post
        val request = call.receive<PhoneCheckBody>()
        val userId = repository.checkPhoneExists(request.phone)
        call.respond(userId ?: "-1")
    }

    post("v1/send-code", {
        tag = "auth"
        protected = true
        description = "Route for receive confirmation code"
        request { body<PhoneBody> { required = true } }
        response { code(HttpStatusCode.OK) {} }
    }) {
        if (!repository.checkAuth(call)) return@post
        val codeSent = repository.sendCode(call.receive<PhoneBody>())
        call.respond(if (codeSent) HttpStatusCode.OK else HttpStatusCode.NotFound)
    }

    post("v1/confirm-code", {
        tag = "auth"
        protected = true
        description = "Route for send confirmation code"
        request { body<CodeBody> { required = true } }
        response { code(HttpStatusCode.OK) {} }
    }) {
        if (!repository.checkAuth(call)) return@post
        val codeSuccess = repository.confirmCode(call.receive<CodeBody>())
        call.respond(if (codeSuccess) HttpStatusCode.OK else HttpStatusCode.NotFound)
    }
}