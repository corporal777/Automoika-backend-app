package kg.automoika

import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.tomcat.*
import kg.automoika.di.dataBaseModule
import kg.automoika.di.mongoModule
import kg.automoika.di.repositoryModule
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        gson {}
//        json(Json {
//            prettyPrint = true
//            isLenient = true
//            ignoreUnknownKeys = true
//        })
    }
    install(Koin) {
        modules(mongoModule(getMongoUri(), getMongoDb()), dataBaseModule, repositoryModule)
    }
    configureDatabases()
    configureFirebase()
    configureSockets()

    configureRouting()
}

private fun Application.getMongoUri(): String {
    return environment.config.propertyOrNull("ktor.mongo.uri")?.getString()
        ?: throw RuntimeException("Failed to access MongoDataBase URI.")
}

private fun Application.getMongoDb(): String {
    return environment.config.propertyOrNull("ktor.mongo.database")?.getString()
        ?: throw RuntimeException("Failed to access MongoDataBase")
}