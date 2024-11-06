package auth.registration.network

import io.ktor.client.HttpClient
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

class RegistrationClient(
    private val httpClient: HttpClient
) {

    suspend fun submitNewUser(request: CreateUserRequest): Result<CreateUserResponse> {
        val response = try {
            httpClient.post("http://10.0.2.2:8082/user") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Result.Error(
                TetherHubError(
                    -1,
                    "TH-0",
                    "Unexpected error during request. Cause: ${e.message}"
                )
            )
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

            else -> Result.Error(
                TetherHubError(
                    response.status.value,
                    "TH-0",
                    "Unexpected http error. Code: ${response.status.value}"
                )
            )
        }
    }
}