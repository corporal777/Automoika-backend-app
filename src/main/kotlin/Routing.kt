package kg.automoika

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kg.automoika.db.CarWashTable
import kg.automoika.db.CodesTable
import kg.automoika.db.TokenTable
import kg.automoika.extensions.EXTERNAL_POINT_IMAGE_PATH
import kg.automoika.extensions.POINTS_LOCAL_IMAGES_DIRECTORY
import kg.automoika.extensions.STATIC_LOCAL_FILES_FOLDER
import kg.automoika.extensions.USERS_LOCAL_IMAGES_DIRECTORY
import kg.automoika.routes.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileInputStream
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    routing {
//        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml") {
//            codegen = StaticHtmlCodegen()
//        }
        route("api.json") {
            openApi()
        }
        route("swagger") {
            swaggerUI("/api.json") {}
        }


        authRoutes()
        carWashRoutes()
        databaseRoutes()

        locationRoutes()
        socketRoutes()
        userRoutes()
        accountRoutes()
        storiesRoutes()
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

    val file = FileInputStream(config.property("firebase.adminSdkFilePath").getString())
    //val file = FileInputStream(config.property("firebase.serverAdminSdkFilePath").getString())

    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(file))
        .setServiceAccountId(config.property("firebase.accountId").getString())
        .build()
    FirebaseApp.initializeApp(options, "ktor-app")
}


fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}