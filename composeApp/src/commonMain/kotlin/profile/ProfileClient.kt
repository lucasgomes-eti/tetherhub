package profile

import io.ktor.client.request.get
import network.HttpClientManager
import network.Resource
import response.ProfileResponse

class ProfileClient(private val httpClientManager: HttpClientManager) {

    suspend fun getProfile(): Resource<ProfileResponse> = httpClientManager.withApiResource {
        get("/profile")
    }
}