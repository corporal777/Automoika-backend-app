package kg.automoika.utils

import com.google.gson.Gson
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import kg.automoika.data.request.LocationRequest
import kg.automoika.data.response.LocationDataResponse
import kg.automoika.data.response.LocationResponse
import kg.automoika.data.response.YandexGeoModel
import kg.automoika.data.response.YandexLocationResponse
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

        call.application.environment
        return city
    }

    suspend fun getLocationFromYandex(request: LocationRequest, call: ApplicationCall): YandexLocationResponse {
        val config = call.application.environment.config
        val url = config.property("yandex.geoCodeUrl").getString()
        val apiKey = config.property("yandex.geoCodeApiKey").getString()
        val client = createHttpClient()
        val response : HttpResponse = client.request(url) {
            method = HttpMethod.Get
            url {
                parameters.append("apikey", apiKey)
                parameters.append("geocode", "${request.lon},${request.lat}")
                parameters.append("lang", "ru")
                parameters.append("format", "json")
            }
        }
        val model : YandexGeoModel = response.body()
        val obj = model.response.GeoObjectCollection.featureMember.find {
            x -> x.GeoObject.metaDataProperty.GeocoderMetaData.kind == "house"
        }


        val components = model.response.GeoObjectCollection.featureMember.firstOrNull()
            ?.GeoObject?.metaDataProperty?.GeocoderMetaData?.Address?.Components
        val country = components?.find { x -> x.kind == "country" }?.name
        val city = components?.find { x -> x.kind == "locality" }?.name

        val streetComponent = model.response.GeoObjectCollection.featureMember
            .find { x -> x.GeoObject.metaDataProperty.GeocoderMetaData.kind == "street" }
            ?.GeoObject?.metaDataProperty?.GeocoderMetaData?.Address
        val street = streetComponent?.Components?.find { x -> x.kind == "street" }?.name


        val houseComponent = model.response.GeoObjectCollection.featureMember
            .find { x -> x.GeoObject.metaDataProperty.GeocoderMetaData.kind == "house" }
        val house = houseComponent?.GeoObject?.metaDataProperty?.GeocoderMetaData?.Address?.Components
            ?.find { x -> x.kind == "house" }?.name

        val districtComponent = model.response.GeoObjectCollection.featureMember
            .find { x -> x.GeoObject.metaDataProperty.GeocoderMetaData.kind == "district" }
        val district = districtComponent?.GeoObject?.metaDataProperty?.GeocoderMetaData?.Address?.Components
            ?.find { x -> x.kind == "district" }?.name

        val location = YandexLocationResponse(
            fullAddress = obj?.GeoObject?.metaDataProperty?.GeocoderMetaData?.text,
            country = country,
            city = city,
            street = street?.trim() + ", " + house,
            district = district
        )
        return location
    }
}