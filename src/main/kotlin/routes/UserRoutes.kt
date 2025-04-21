package kg.automoika.routes

import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.automoika.data.body.*
import kg.automoika.data.response.DataResponse
import kg.automoika.data.response.ErrorResponse
import kg.automoika.data.response.TokenResponse
import kg.automoika.data.response.UserResponse
import kg.automoika.extensions.body
import kg.automoika.extensions.tag
import kg.automoika.repository.AuthRepository
import kg.automoika.repository.UserRepository
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val repository by inject<UserRepository>()
    val auth by inject<AuthRepository>()

    get("v1/user/{id}", {
        tag = "user"
        protected = true
        description = "Route for get user by id"
        request { pathParameter<String>("id") }
        response { body<UserResponse>(HttpStatusCode.OK) }
    }) {
        if (!auth.checkAuth(call)) return@get
        val id = call.parameters["id"]
        val response = repository.getUserById(id)
        call.respond(response ?: HttpStatusCode.NotFound)
    }

    post("v1/login-user", {
        tag = "user"
        protected = true
        description = "Route for login user"
        request { body<LoginBody>() }
        response {
            code(HttpStatusCode.OK) {
                description = "Success response"
                body<UserResponse>()
            }
            code(HttpStatusCode.NotFound) {
                description = "Error response"
                body<ErrorResponse>(){
                    example("Error 1") {
                        description = "User not found"
                        value = ErrorResponse("User not found")
                    }
                    example("Error 2") {
                        description = "Password is not valid"
                        value = ErrorResponse("Password is not valid")
                    }
                }
            }
        }
    }) {
        if (!auth.checkAuth(call)) return@post
        val request = call.receive<LoginBody>()
        val response = repository.loginUser(request, call) ?: return@post
        call.respond(response)
    }


    post("v1/create-user", {
        tag = "user"
        protected = true
        description = "Route for register user"
        request { body<UserBody>() }
        response { body<UserResponse>(HttpStatusCode.Created) }
    }) {
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