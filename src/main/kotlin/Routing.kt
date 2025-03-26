package kg.automoika

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.utils.io.*
import kg.automoika.db.CarWashTable
import kg.automoika.db.CodesTable
import kg.automoika.db.TokenTable
import kg.automoika.extensions.*
import kg.automoika.routes.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileInputStream
import java.time.Duration
import kotlinx.coroutines.*

fun Application.configureRouting() {
    routing {
        carWashRoutes()
        databaseRoutes()
        authRoutes()
        locationRoutes()
        socketRoutes()
        userRoutes()
        configureStaticFolders()
    }
}

fun Routing.configureStaticFolders() {
    static("/") {
        staticRootFolder = File(STATIC_LOCAL_FILES_FOLDER)
        static(EXTERNAL_POINT_IMAGE_PATH) {
            files(POINTS_LOCAL_IMAGES_DIRECTORY)
            files(USERS_LOCAL_IMAGES_DIRECTORY)
        }
    }
}

fun Application.configureDatabases() {
    val config = environment.config
    val db = Database.connect(
        url = config.property("storage.jdbcURL").getString(),
        user = config.property("storage.user").getString(),
        password = config.property("storage.password").getString(),
        driver = config.property("storage.driverClassName").getString()
    )
    transaction(db) {
        SchemaUtils.create(CarWashTable, TokenTable, CodesTable)
    }
}

fun Application.configureFirebase() {
    val config = environment.config

    //val file = FileInputStream(config.property("firebase.adminSdkFilePath").getString())
    val file = FileInputStream(config.property("firebase.serverAdminSdkFilePath").getString())

    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(file))
        .setServiceAccountId(config.property("firebase.accountId").getString())
        .build()
    FirebaseApp.initializeApp(options, "ktor-app")
}


fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}