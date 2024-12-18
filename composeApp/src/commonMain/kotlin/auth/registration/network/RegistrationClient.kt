package auth.registration.network

import HttpClientManager
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import request.CreateUserRequest
import response.CreateUserResponse
import response.TetherHubError
import util.Result
import util.unexpectedErrorWithException
import util.unexpectedErrorWithHttpStatusCode

class RegistrationClient(
    private val httpClientManager: HttpClientManager
) {

    suspend fun submitNewUser(request: CreateUserRequest): Result<CreateUserResponse> {
        val response = try {
            httpClientManager.client.post("/user") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Result.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val result = response.body<CreateUserResponse>()
                Result.Success(result)
            }

            in 400..599 -> {
                val errorResponse = response.body<TetherHubError>()
                Result.Error(errorResponse)
            }

            else -> Result.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }
}