package messages

import Message
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import network.HttpClientManager
import network.Resource
import response.ChatResponse

class ChatClient(private val httpClientManager: HttpClientManager) {

    private val outgoingMessage = Channel<Message>()

    suspend fun getRooms(): Resource<List<ChatResponse>> = httpClientManager.withApiResource {
        get("chats")
    }

    suspend fun sendMessage(message: Message) {
        outgoingMessage.send(message)
    }

    fun connectToChat(chatId: String): Flow<Message> = flow {
        httpClientManager.client.webSocket(
            method = HttpMethod.Get,
            host = httpClientManager.baseUrl.host,
            port = httpClientManager.baseUrl.port,
            path = "/chats/$chatId"
        ) {
            launch {
                outgoingMessage.receiveAsFlow().collect {
                    sendSerialized(it)
                }
            }
            while (currentCoroutineContext().isActive) {
                emit(receiveDeserialized<Message>())
            }
        }
    }

    suspend fun getChatById(chatId: String): Resource<ChatResponse> =
        httpClientManager.withApiResource {
            get("chats/$chatId")
        }
}