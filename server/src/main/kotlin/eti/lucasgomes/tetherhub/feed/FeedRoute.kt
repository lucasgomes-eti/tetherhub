package eti.lucasgomes.tetherhub.feed

import arrow.core.Either
import eti.lucasgomes.tetherhub.userEmail
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import request.CreatePostRequest

fun Route.feedRoutes() {
    val feedService by inject<FeedService>()
    route("feed") {
        post {
            val createPostRequest = try {
                call.receive<CreatePostRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, FeedErrors.InvalidParameters)
                return@post
            }
            when (val createResponse = feedService.savePost(createPostRequest, userEmail)) {
                is Either.Left -> call.respond(
                    HttpStatusCode.fromValue(createResponse.value.httCode),
                    createResponse.value
                )

                is Either.Right -> call.respond(HttpStatusCode.Created, createResponse.value)
            }
        }
        get {
            call.respond(feedService.findAll())
        }
    }
}