package eti.lucasgomes.tetherhub.post

import arrow.core.Either
import eti.lucasgomes.tetherhub.dsl.userEmail
import eti.lucasgomes.tetherhub.dsl.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject
import request.CreatePostRequest
import request.PatchPostContentRequest

fun Route.postRoutes() {
    val postService by inject<PostService>()
    route("posts") {
        post {
            val createPostRequest = try {
                call.receive<CreatePostRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, PostErrors.InvalidParameters)
                return@post
            }
            when (val createResponse = postService.savePost(createPostRequest, userEmail)) {
                is Either.Left -> call.respond(
                    HttpStatusCode.fromValue(createResponse.value.httpCode),
                    createResponse.value
                )

                is Either.Right -> call.respond(HttpStatusCode.Created, createResponse.value)
            }
        }
        get {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            val size = call.request.queryParameters["size"]?.toInt() ?: 20
            call.respond(postService.findAll(userId = userId, page = page, size = size))
        }
        get("{postId}") {
            val postId = try {
                ObjectId(call.parameters["postId"])
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, PostErrors.InvalidParameters)
                return@get
            }
            postService.findById(postId = postId, userId = userId)
                .onLeft {
                    call.respond(HttpStatusCode.fromValue(it.httpCode), it)
                }.onRight {
                    call.respond(it)
                }
        }
        post("{postId}/toggle_like") {
            val postId = try {
                ObjectId(call.parameters["postId"])
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, PostErrors.InvalidParameters)
                return@post
            }
            when (val result = postService.toggleLike(postId = postId, userId = userId)) {
                is Either.Left -> call.respond(
                    HttpStatusCode.fromValue(result.value.httpCode),
                    result.value
                )

                is Either.Right -> call.respond(result.value)
            }
        }
        get("my_posts") {
            call.respond(postService.findPostsByAuthor(userId))
        }
        patch("{postId}") {
            val postId = try {
                ObjectId(call.parameters["postId"])
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, PostErrors.InvalidParameters)
                return@patch
            }
            val newContent = try {
                call.receive<PatchPostContentRequest>().content
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, PostErrors.InvalidParameters)
                return@patch
            }
            when (val result = postService.editContent(
                postId = postId,
                userId = userId,
                newContent = newContent
            )) {
                is Either.Left -> call.respond(
                    HttpStatusCode.fromValue(result.value.httpCode),
                    result.value
                )

                is Either.Right -> call.respond(result.value)
            }
        }
        delete("{postId}") {
            val postId = try {
                ObjectId(call.parameters["postId"])
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, PostErrors.InvalidParameters)
                return@delete
            }
            when (val result = postService.deletePost(postId, userId = userId)) {
                is Either.Left -> call.respond(
                    HttpStatusCode.fromValue(result.value.httpCode),
                    result.value
                )

                is Either.Right -> call.respond(status = HttpStatusCode.NoContent, Unit)
            }
        }
    }
}