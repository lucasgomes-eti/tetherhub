package feed

import HttpClientManager
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import request.CreatePostRequest
import response.PostResponse
import response.TetherHubError
import util.Result
import util.unexpectedErrorWithException
import util.unexpectedErrorWithHttpStatusCode
import kotlin.coroutines.cancellation.CancellationException

class FeedClient(private val httpClientManager: HttpClientManager) {

    suspend fun getPosts(): Result<List<PostResponse>> {
        val response = try {
            httpClientManager.client.get("/feed")
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Result.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                Result.Success(response.body<List<PostResponse>>())
            }

            in 400..599 -> {
                Result.Error(response.body<TetherHubError>())
            }

            else -> Result.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }

    suspend fun publishPost(createPostRequest: CreatePostRequest): Result<PostResponse> {
        val response = try {
            httpClientManager.client.post("/feed") {
                contentType(ContentType.Application.Json)
                setBody(createPostRequest)
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Result.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                Result.Success(response.body<PostResponse>())
            }

            in 400..599 -> {
                Result.Error(response.body<TetherHubError>())
            }

            else -> Result.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }
}