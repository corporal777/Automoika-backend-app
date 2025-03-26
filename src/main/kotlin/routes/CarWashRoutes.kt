package kg.automoika.routes

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.automoika.di.AppData
import kg.automoika.data.body.CarWashBody
import kg.automoika.data.body.CarWashBody.Companion.setBackgroundImage
import kg.automoika.data.body.CarWashBody.Companion.setData
import kg.automoika.data.body.CarWashFreeBoxesBody
import kg.automoika.data.remote.CarWashImageModel
import kg.automoika.data.response.CarWashShortResponse
import kg.automoika.data.response.DataResponse
import kg.automoika.utils.FileUtils.uploadImageToFirebase
import kg.automoika.extensions.generateId
import kg.automoika.extensions.generateShortId
import kg.automoika.repository.AuthRepository
import kg.automoika.repository.CarWashRepository
import org.koin.ktor.ext.inject

fun Route.carWashRoutes() {
    val repository by inject<CarWashRepository>()
    val auth by inject<AuthRepository>()
    val appData by inject<AppData>()

    get("v1/car-wash-detail/{id}") {
        if (!auth.checkAuth(call)) return@get

        val params = call.request.queryParameters
        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val response = repository.getCarWashById(id, params)
        if (response != null) call.respond(response)
        else call.respond(HttpStatusCode.NotFound)
    }

    get("v1/car-wash-list") {
        if (!auth.checkAuth(call)) return@get

        val params = call.request.queryParameters
        val dataList = repository.getCarWashList(params)

        if (dataList.isEmpty()) call.respond(DataResponse.fromList(emptyList<CarWashShortResponse>()))
        else call.respond(DataResponse.fromList(dataList))
    }


    post("v1/create-car-wash") {
        if (!auth.checkAuth(call)) return@post

        val imagesList = arrayListOf<CarWashImageModel>()
        val carWashBody = CarWashBody(generateId())
        try {
            call.receiveMultipart().forEachPart { partData ->
                if (partData is PartData.FormItem) carWashBody.setData(partData)
                else if (partData is PartData.FileItem){
                    val fileName = "car-wash-${carWashBody.id}-image-${generateShortId()}"
                    val fileUrl = uploadImageToFirebase(partData, fileName)
                    if (partData.name == "backgroundImage") carWashBody.setBackgroundImage(fileName, fileUrl)
                    else imagesList.add(CarWashImageModel(fileName, fileUrl))
                } else return@forEachPart
            }

            val response = repository.createCarWashPoint(carWashBody, imagesList)
            if (response != null) call.respond(HttpStatusCode.Created, response)
            else call.respond(HttpStatusCode.BadRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
        }
    }


    post("v1/send-free-boxes") {
        if (!auth.checkAuth(call)) return@post

        val request = call.receive<CarWashFreeBoxesBody>()
        try {
            val update = repository.updateBoxes(request)
            if (update) appData.sendMessage(Gson().toJson(request))
            call.respond(HttpStatusCode.OK)
        } catch (e : Exception){
            call.respond(HttpStatusCode.BadRequest)
        }
    }

}

