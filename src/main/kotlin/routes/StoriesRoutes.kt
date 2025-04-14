package kg.automoika.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.automoika.repository.CommonRepository
import kg.automoika.repository.StoriesRepository
import org.koin.ktor.ext.inject

fun Route.storiesRoutes() {
    val repository by inject<StoriesRepository>()

    get("v1/stories") {
        val list = repository.getStories()
        if (list.isEmpty()) call.respond(HttpStatusCode.NotFound)
        else call.respond(HttpStatusCode.OK, list)
    }
}