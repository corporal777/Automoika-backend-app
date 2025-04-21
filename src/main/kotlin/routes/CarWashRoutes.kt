package kg.automoika.routes

import com.google.gson.Gson
import io.github.smiley4.ktoropenapi.get
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kg.automoika.data.body.CarWashBody
import kg.automoika.data.body.CarWashBody.Companion.setBackgroundImage
import kg.automoika.data.body.CarWashBody.Companion.setData
import kg.automoika.data.body.CarWashFreeBoxesBody
import kg.automoika.data.remote.CarWashImageModel
import kg.automoika.data.response.CarWashFullResponse
import kg.automoika.data.response.CarWashShortResponse
import kg.automoika.data.response.DataResponse
import kg.automoika.data.response.UserResponse
import kg.automoika.di.AppData
import kg.automoika.extensions.body
import kg.automoika.extensions.generateId
import kg.automoika.extensions.generateShortId
import kg.automoika.extensions.tag
import kg.automoika.repository.AuthRepository
import kg.automoika.repository.CarWashRepository
import kg.automoika.utils.FileUtils.uploadImageToFirebase
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject



fun Route.carWashRoutes() {
    val repository by inject<CarWashRepository>()
    val auth by inject<AuthRepository>()
    val appData by inject<AppData>()


    get("v1/car-wash-user/{id}") {
        if (!auth.checkAuth(call)) return@get

        val id = call.parameters.getOrFail("id")
        val response = repository.getCarWashUser(id)
        if (response != null) call.respond(response)
        else call.respond(HttpStatusCode.NotFound)
    }


    get("v1/car-wash-detail/{id}", {
        tag = "car-wash"
        protected = true
        description = "Route for get car wash by id"
        request {
            pathParameter<String>("id")
            queryParameter<String>("reviewShort") { description = "short review param" }
        }
        response { body<CarWashFullResponse>(HttpStatusCode.OK) }
    }) {
        if (!auth.checkAuth(call)) return@get
        val params = call.request.queryParameters
        val id = call.parameters.getOrFail("id")
        val response = repository.getCarWashById(id, params)
        call.respond(response ?: HttpStatusCode.NotFound)
    }

    get("v1/car-wash-list", {
        tag = "car-wash"
        protected = true
        description = "Route for get car wash list"
        request {
            queryParameter<String>("search") { description = "search text param" }
            queryParameter<String>("boxes") { description = "only free boxes param" }
            queryParameter<String>("district") { description = "search by district param" }
            queryParameter<String>("type") { description = "search by type param" }
            queryParameter<String>("limit") { description = "limit param" }
            queryParameter<String>("offset") { description = "offset param" }
        }
        response { body<DataResponse<CarWashShortResponse>>(HttpStatusCode.OK) }
    }) {
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

