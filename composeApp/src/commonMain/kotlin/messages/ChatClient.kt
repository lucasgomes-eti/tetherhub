package messages

import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import network.HttpClientManager
import network.Resource
import request.CreateChatRequest
import request.MessageRequest
import response.ChatResponse
import response.MessageResponse
import response.MessageType
import kotlin.time.ExperimentalTime

class ChatClient(private val httpClientManager: HttpClientManager) {

    private val outgoingMessageRequest = Channel<MessageRequest>()

    suspend fun createRoom(room: CreateChatRequest): Resource<ChatResponse> =
        httpClientManager.withApiResource {
            post("chats") {
                contentType(ContentType.Application.Json)
                setBody(room)
            }
        }

    suspend fun getRooms(): Resource<List<ChatResponse>> = httpClientManager.withApiResource {
        get("chats")
    }

    suspend fun sendMessage(messageRequest: MessageRequest) {
        outgoingMessageRequest.send(messageRequest)
    }

    @OptIn(ExperimentalTime::class)
    fun connectToChat(chatId: String): Flow<MessageResponse> = flow {
        try {
            httpClientManager.getClient().webSocket(
                urlString = "wss://${httpClientManager.baseUrl.host}/chats/$chatId",
            ) {
                launch {
                    outgoingMessageRequest.receiveAsFlow().collect {
                        sendSerialized(it)
                    }
                }
                while (currentCoroutineContext().isActive) {
                    val message = receiveDeserialized<MessageResponse>()
                    emit(message)
                }
                close(CloseReason(CloseReason.Codes.NORMAL, "Chat closed"))
            }
        } catch (e: Exception) {
            if (e is CancellationException) return@flow
            emit(
                MessageResponse(
                    senderId = "tetherhub",
                    senderUsername = "tetherhub",
                    content = "Disconnected from server. Cause: ${e.message}",
                    at = kotlin.time.Clock.System.now(),
                    type = MessageType.SYSTEM
                )
            )
        }

    }

    suspend fun getChatById(chatId: String): Resource<ChatResponse> =
        httpClientManager.withApiResource {
            get("chats/$chatId")
        }
}