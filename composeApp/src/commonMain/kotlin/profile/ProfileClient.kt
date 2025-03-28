package profile

import io.ktor.client.request.get
import network.HttpClientManager
import network.Resource
import response.PageResponse
import response.ProfileResponse
import response.PublicProfileResponse

class ProfileClient(private val httpClientManager: HttpClientManager) {

    suspend fun getProfile(): Resource<ProfileResponse> = httpClientManager.withApiResource {
        get("profiles/my_profile")
    }

    suspend fun getProfilesByUsername(
        query: String,
        page: Int,
        size: Int = 50
    ): Resource<PageResponse<PublicProfileResponse>> =
        httpClientManager.withApiResource {
            get("profiles?username=$query&page=$page&size=$size")
        }
}