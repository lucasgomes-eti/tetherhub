package auth.registration.network

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import network.HttpClientManager
import network.Resource
import network.unexpectedErrorWithException
import network.unexpectedErrorWithHttpStatusCode
import request.CreateUserRequest
import response.CreateUserResponse
import response.TetherHubError

class RegistrationClient(
    private val httpClientManager: HttpClientManager
) {

    suspend fun submitNewUser(request: CreateUserRequest): Resource<CreateUserResponse> {
        val response = try {
            httpClientManager.client.post("/user") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Resource.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val result = response.body<CreateUserResponse>()
                Resource.Success(result)
            }

            in 400..599 -> {
                val errorResponse = response.body<TetherHubError>()
                Resource.Error(errorResponse)
            }

            else -> Resource.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }
}