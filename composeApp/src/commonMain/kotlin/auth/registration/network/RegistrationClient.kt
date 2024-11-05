package auth.registration.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import request.CreateUserRequest
import response.CreateUserResponse
import util.NetworkError
import util.Result

class RegistrationClient(
    private val httpClient: HttpClient
) {

    suspend fun submitNewUser(request: CreateUserRequest): Result<CreateUserResponse, NetworkError> {
        val response = try {
            httpClient.post("http://10.0.2.2:8082/user") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val result = response.body<CreateUserResponse>()
                Result.Success(result)
            }

            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}