package messages

import io.ktor.client.request.get
import network.HttpClientManager
import network.Resource
import response.CreateChatResponse

class ChatClient(private val httpClientManager: HttpClientManager) {

    suspend fun getRooms(): Resource<List<CreateChatResponse>> = httpClientManager.withApiResource {
        get("chats")
    }
}