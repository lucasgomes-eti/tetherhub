package profile

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException
import network.HttpClientManager
import network.Resource
import network.unexpectedErrorWithException
import network.unexpectedErrorWithHttpStatusCode
import response.ProfileResponse
import response.TetherHubError

class ProfileClient(private val httpClientManager: HttpClientManager) {

    suspend fun getProfile(): Resource<ProfileResponse> {
        val response = try {
            httpClientManager.client.get("/profile")
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Resource.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                Resource.Success(response.body<ProfileResponse>())
            }

            in 400..599 -> {
                Resource.Error(response.body<TetherHubError>())
            }

            else -> Resource.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }
}