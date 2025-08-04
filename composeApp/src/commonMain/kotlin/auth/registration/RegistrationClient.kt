package auth.registration

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import network.EmptyResult
import network.HttpClientManager
import network.Resource
import request.CreateUserRequest
import request.FcmTokenRequest
import response.UserResponse

class RegistrationClient(
    private val httpClientManager: HttpClientManager
) {

    suspend fun submitNewUser(request: CreateUserRequest): Resource<UserResponse> =
        httpClientManager.withApiResource {
            post("/users") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun registerFcmTokenForUser(request: FcmTokenRequest): EmptyResult =
        httpClientManager.withApiResource {
            post("/users/register_fcm_token") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}