package kg.automoika

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthKeyLocation
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.tomcat.jakarta.*
import kg.automoika.di.dataBaseModule
import kg.automoika.di.mongoModule
import kg.automoika.di.repositoryModule
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            setLenient()
        }
    }
    install(Koin) {
        modules(mongoModule(getMongoUri(), getMongoDb()), dataBaseModule, repositoryModule)
    }

    install(OpenApi){
        tags {
            tag("user"){}
            tag("auth") {}
            tag("car-wash") {}
        }
        security {
            defaultUnauthorizedResponse {
                description = "Authentication is FAIL"
            }
            defaultSecuritySchemeNames("Authorization")
            securityScheme("Authorization") {
                name = "Authorization"
                location = AuthKeyLocation.HEADER
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "JWT"
                description = "Authorization: Bearer [token]"
            }
        }
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