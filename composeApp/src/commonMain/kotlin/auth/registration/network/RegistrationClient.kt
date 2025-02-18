package auth.registration.network

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import network.HttpClientManager
import network.Resource
import request.CreateUserRequest
import response.UserResponse

class RegistrationClient(
    private val httpClientManager: HttpClientManager
) {

    suspend fun submitNewUser(request: CreateUserRequest): Resource<UserResponse> =
        httpClientManager.withApiResource {
            post("/user") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}