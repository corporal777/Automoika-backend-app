package kg.automoika.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.automoika.data.body.*
import kg.automoika.data.response.DataResponse
import kg.automoika.repository.AuthRepository
import kg.automoika.repository.UserRepository
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val repository by inject<UserRepository>()
    val auth by inject<AuthRepository>()

    get("v1/user/{id}"){
        if (!auth.checkAuth(call)) return@get

        val id = call.parameters["id"]
        val response = repository.getUserById(id)
        call.respond(response ?: HttpStatusCode.NotFound)
    }

    post("v1/login-user"){
        if (!auth.checkAuth(call)) return@post

        val request = call.receive<LoginBody>()
        val response = repository.loginUser(request, call) ?: return@post
        call.respond(response)
    }

    post("v1/create-user") {
        if (!auth.checkAuth(call)) return@post

        val request = call.receive<UserBody>()
        val response = repository.createUser(request)
        if (response != null) call.respond(HttpStatusCode.Created, response)
        else call.respond(HttpStatusCode.BadRequest)
    }

    post("v1/send-review") {
        if (!auth.checkAuth(call)) return@post

        val request = call.receive<ReviewBody>()
        val response = repository.sendReview(request)
        if (response != null) call.respond(HttpStatusCode.Created, response)
        else call.respond(HttpStatusCode.BadRequest)
    }

    get("v1/car-wash-reviews/{id}") {
        if (!auth.checkAuth(call)) return@get

        val id = call.parameters["id"]
        val response = repository.getCarWashReviews(id)
        call.respond(HttpStatusCode.OK, DataResponse.fromList(response))
    }
}