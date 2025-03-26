package kg.automoika.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kg.automoika.di.AppData
import kotlinx.coroutines.flow.collectLatest
import org.koin.ktor.ext.inject

fun Route.socketRoutes() {
    val appData by inject<AppData>()

    webSocket("/car-wash-point"){
        try {
            send("Connected to WebSocket")
            appData.sharedFlow.collectLatest { message ->
                send(message)
            }
        } catch (e : Exception){

        }

    }
}
