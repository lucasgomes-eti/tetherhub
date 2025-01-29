package auth.login.network

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
import response.AuthResponse
import response.TetherHubError

class LoginClient(
    private val httpClientManager: HttpClientManager
) {
    suspend fun authenticateWithCredentials(
        email: String,
        password: String
    ): Resource<AuthResponse> {
        val response = try {
            httpClientManager.client.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(hashMapOf("email" to email, "password" to password))
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Resource.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val result = response.body<AuthResponse>()
                createUserSession(result)
                Resource.Success(result)
            }

            in 400..599 -> {
                val errorResponse = response.body<TetherHubError>()
                Resource.Error(errorResponse)
            }

            else -> Resource.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }

    private fun createUserSession(auth: AuthResponse) {
        httpClientManager.installAuth(auth.token)
    }
}