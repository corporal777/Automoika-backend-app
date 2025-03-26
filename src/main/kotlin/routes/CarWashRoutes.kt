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
import kg.automoika.extensions.FileUtils.uploadImageToFirebase
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

        val id = call.parameters["id"]
        if (id.isNullOrEmpty()) call.respond(HttpStatusCode.BadRequest)
        else {
            val carWashDetail = repository.getCarWashById(id)
            if (carWashDetail != null) call.respond(carWashDetail)
            else call.respond(HttpStatusCode.NotFound)
        }
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



    post("v1/upload-point-images") {
//        val client = createHttpClient()
//        val imagesList = arrayListOf<String>()
//        var count = 0
//        call.receiveMultipart().forEachPart { part ->
//            if (part is PartData.FileItem) {
//                call.application.environment.log.error(part.originalFileName)
//                val fileBytes = part.streamProvider().readBytes()
//                imagesList.add(uploadImageToFirebase(client, fileBytes, part.originalFileName ?: "image.jpeg", count))
//                count++
//            }
//            part.dispose()
//        }
//        client.close()
//
//
//        if (imagesList.isEmpty()) {
//            call.respond(HttpStatusCode.BadRequest, "Image Not Uploaded")
//        } else call.respond(HttpStatusCode.OK)
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

