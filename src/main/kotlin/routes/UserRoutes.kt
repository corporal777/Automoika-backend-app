package kg.automoika.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.automoika.data.body.*
import kg.automoika.data.body.CarWashBody.Companion.setBackgroundImage
import kg.automoika.data.body.CarWashBody.Companion.setData
import kg.automoika.data.remote.CarWashImageModel
import kg.automoika.data.response.DataResponse
import kg.automoika.extensions.BASE_URL
import kg.automoika.extensions.EXTERNAL_POINT_IMAGE_PATH
import kg.automoika.extensions.FileUtils.saveImageLocal
import kg.automoika.extensions.POINTS_LOCAL_IMAGE_FULL_PATH
import kg.automoika.extensions.generateId
import kg.automoika.repository.AuthRepository
import kg.automoika.repository.UserRepository
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val repository by inject<UserRepository>()
    val auth by inject<AuthRepository>()

    post("v1/create-google-user"){
        if (!auth.checkAuth(call)) return@post

        val request = call.receive<UserGoogleBody>()
        val response = repository.createGoogleUser(request)
        if (response != null) call.respond(HttpStatusCode.Created, response)
        else call.respond(HttpStatusCode.BadRequest)
    }

    post("v1/create-user") {
        if (!auth.checkAuth(call)) return@post

        try {
            val userBody = UserBody(generateId())
            call.receiveMultipart().forEachPart { partData ->
                if (partData is PartData.FormItem) userBody.setData(partData)
                else if (partData is PartData.FileItem) userBody.setImage(partData)
                else return@forEachPart
            }
            val response = repository.createUser(userBody)
            if (response != null) call.respond(HttpStatusCode.Created, response)
            else call.respond(HttpStatusCode.BadRequest)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
        }
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