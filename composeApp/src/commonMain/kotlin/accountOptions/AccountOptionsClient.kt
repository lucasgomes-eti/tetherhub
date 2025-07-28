package accountOptions

import io.ktor.client.request.delete
import network.EmptyResult
import network.HttpClientManager

class AccountOptionsClient(private val httpClientManager: HttpClientManager) {
    suspend fun logOut() = httpClientManager.logOut()
    suspend fun deleteAccount(): EmptyResult = httpClientManager.withApiResource {
        delete("/users")
    }
}