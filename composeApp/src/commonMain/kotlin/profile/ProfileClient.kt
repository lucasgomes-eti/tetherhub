package profile

import HttpClientManager
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException
import response.ProfileResponse
import response.TetherHubError
import util.Result
import util.unexpectedErrorWithException
import util.unexpectedErrorWithHttpStatusCode

class ProfileClient(private val httpClientManager: HttpClientManager) {

    suspend fun getProfile(): Result<ProfileResponse> {
        val response = try {
            httpClientManager.client.get("/profile")
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Result.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                Result.Success(response.body<ProfileResponse>())
            }

            in 400..599 -> {
                Result.Error(response.body<TetherHubError>())
            }

            else -> Result.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }
}