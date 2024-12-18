package auth.login.network

import HttpClientManager
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import response.AuthResponse
import response.TetherHubError
import util.Result
import util.unexpectedErrorWithException
import util.unexpectedErrorWithHttpStatusCode

class LoginClient(
    private val httpClientManager: HttpClientManager
) {
    suspend fun authenticateWithCredentials(
        email: String,
        password: String
    ): Result<AuthResponse> {
        val response = try {
            httpClientManager.client.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(hashMapOf("email" to email, "password" to password))
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Result.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val result = response.body<AuthResponse>()
                createUserSession(result)
                Result.Success(result)
            }

            in 400..599 -> {
                val errorResponse = response.body<TetherHubError>()
                Result.Error(errorResponse)
            }

            else -> Result.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }

    private fun createUserSession(auth: AuthResponse) {
        httpClientManager.installAuth(auth.token)
    }
}