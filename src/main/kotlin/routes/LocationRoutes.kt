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
import kg.automoika.utils.LocationUtils
import org.koin.ktor.ext.inject
import java.awt.Window

fun Route.locationRoutes() {
    val repository by inject<CarWashRepository>()
    val auth by inject<AuthRepository>()

    post("v1/check-user-location") {
        if (!auth.checkAuth(call)) return@post

        val location = call.receive<LocationRequest>()
        val response = LocationUtils.getLocationFromResource(location, call)
        if (response != null) call.respond(response)
        else call.respond(HttpStatusCode.NotFound, "Location not found")
    }

    post("v1/location-info") {
        if (!auth.checkAuth(call)) return@post

        val location = call.receive<LocationRequest>()
        val response = LocationUtils.getLocationFromYandex(location, call)
        if (response != null) call.respond(response)
        else call.respond(HttpStatusCode.NotFound, "Location not found")
    }
}


