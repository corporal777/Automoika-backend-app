package kg.automoika.routes

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.automoika.data.request.*
import kg.automoika.data.response.DataResponse
import kg.automoika.data.response.LocationDataResponse
import kg.automoika.data.response.LocationResponse
import kg.automoika.extensions.createHttpClient
import kg.automoika.extensions.distanceInKm
import kg.automoika.repository.AuthRepository
import kg.automoika.repository.CarWashRepository
import org.koin.ktor.ext.inject
import java.awt.Window

fun Route.locationRoutes() {
    val repository by inject<CarWashRepository>()
    val auth by inject<AuthRepository>()

    post("v1/check-user-location") {
        if (!auth.checkAuth(call)) return@post

        val location = call.receiveNullable<LocationRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Body cannot be null!")
            return@post
        }
        val response = call.getLocationFromResource(location)
        if (response != null) call.respond(response)
        else call.respond(HttpStatusCode.NotFound, "Location not found")
    }
}


private fun ApplicationCall.getLocationFromResource(request: LocationRequest) : LocationResponse? {
    val fileContent = this::class.java.classLoader.getResource("kg-cities.json")?.readText()
    val citiesList =  Gson().fromJson(fileContent, LocationDataResponse::class.java).data

    val city = citiesList.find { x ->
        distanceInKm(
            x.lat.toDouble(),
            x.lon.toDouble(),
            request.lat.toDouble(),
            request.lon.toDouble()
        ) <= 30
    }

    return city
}