package kg.automoika.extensions

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get
import java.util.*
import kotlin.random.Random


fun generateShortId(): String {
    return Random.nextInt(0, 900000).toString()
}

fun generateCode(): String {
    return Random.nextInt(100000, 900000).toString()
}

fun generateId(): String {
    return Random.nextInt(0, 900000000).toString()
}

fun generateToken(): String {
    val newToken = UUID.randomUUID().toString()
    return newToken.replace("-", "")
}

fun createHttpClient(): HttpClient {
    return HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        followRedirects = false
    }
}

suspend fun HttpClient.submitFormWithBinaryData(url: String, bytes: ByteArray, name: String): HttpResponse {
    return submitFormWithBinaryData(
        url = url,
        formData = formData {
            append("image", bytes, Headers.build {
                append(HttpHeaders.ContentType, "image/png")
                append(HttpHeaders.ContentDisposition, "filename=\"$name\"")
            })
        }
    )
}

fun String.hasText(text : String): Boolean {
    return contains(text, true)
}

fun daysBetween(dateOne: Long, dateTwo: Long): Int {
    var days = 0
    for (i in dateOne..dateTwo step 86400000) days++
    return days
}

fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val theta = lon1 - lon2
    var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
    dist = Math.acos(dist)
    dist = rad2deg(dist)
    dist *= 60 * 1.1515
    dist *= 1.609344
    return dist
}

private fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

private fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}

suspend fun <T> suspendTransaction(block: suspend () -> T): T {
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}