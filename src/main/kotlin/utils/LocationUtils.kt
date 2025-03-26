package kg.automoika.utils

import com.google.gson.Gson
import io.ktor.server.application.*
import kg.automoika.data.request.LocationRequest
import kg.automoika.data.response.LocationDataResponse
import kg.automoika.data.response.LocationResponse
import kg.automoika.extensions.createHttpClient
import kg.automoika.extensions.distanceInKm

object LocationUtils {

    fun getLocationFromResource(request: LocationRequest, call: ApplicationCall) : LocationResponse? {
        val fileContent = call::class.java.classLoader.getResource("kg-cities.json")?.readText()
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

    fun getLocationFromYandex(request: LocationRequest){
        val client = createHttpClient()


    }
}