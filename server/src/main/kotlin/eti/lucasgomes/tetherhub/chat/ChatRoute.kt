package eti.lucasgomes.tetherhub.chat

import eti.lucasgomes.tetherhub.dsl.userId
import eti.lucasgomes.tetherhub.dsl.username
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject
import request.CreateChatRequest
import request.MessageRequest
import response.ChatResponse
import response.MessageResponse
import response.MessageType
import response.UserResponse
import java.util.Collections

data class ServerRooms(
    val chat: ChatResponse,
    val connectedSessions: MutableList<WebSocketServerSession>
)

fun Route.chatRoutes() {
    val chatService by inject<ChatService>()
    route("chats") {
        val serverRooms = Collections.synchronizedList<ServerRooms>(ArrayList())

        post {
            val createChatRequest = try {
                call.receive<CreateChatRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ChatErrors.InvalidBody)
                return@post
            }
            chatService.createChat(createChatRequest).onLeft {
                call.respond(HttpStatusCode.fromValue(it.httpCode), it)
            }.onRight { call.respond(HttpStatusCode.Created, it) }
        }

        get {
            chatService.findRoomsByUserId(userId).onLeft {
                call.respond(HttpStatusCode.fromValue(it.httpCode), it)
            }.onRight { call.respond(it) }
        }

        get("{chatId}") {
            val chatId = call.parameters["chatId"]?.let { ObjectId(it) } ?: run {
                call.respond(HttpStatusCode.BadRequest, ChatErrors.MissingParameter)
                return@get
            }
            chatService.findById(chatId).onLeft {
                call.respond(HttpStatusCode.fromValue(it.httpCode), it)
            }.onRight { call.respond(it) }
        }

        webSocket("{chatId}") {

            val chatId = call.parameters["chatId"]?.let { ObjectId(it) } ?: run {
                close(CloseReason(CloseReason.Codes.NORMAL, "Missing parameter chatId"))
                return@webSocket
            }

            chatService.findById(chatId).onLeft {
                close(CloseReason(CloseReason.Codes.NORMAL, it.message))
            }.onRight { chat ->
                val roomIndex =
                    serverRooms.indexOfFirst { it.chat.chatId == chatId.toString() }.let {
                        return@let if (it != -1) {
                            serverRooms[it].connectedSessions.add(this)
                            it
                        } else {
                            serverRooms.addLast(ServerRooms(chat, mutableListOf(this)))
                            serverRooms.lastIndex
                        }
                    }

                serverRooms[roomIndex].connectedSessions.forEach {
                    it.sendSerialized(
                        MessageResponse(
                            senderId = "tetherhub",
                            content = "${call.username} Joined the chat. Online members: ${serverRooms[roomIndex].connectedSessions.size}",
                            at = Clock.System.now(),
                            type = MessageType.SYSTEM
                        )
                    )
                }

                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    launch {
                        val serializedMessage = frame.data.toString(Charsets.UTF_8)
                        val newMessageRequest =
                            Json.decodeFromString<MessageRequest>(serializedMessage)
                        for (session in serverRooms[roomIndex].connectedSessions) {
                            session.sendSerialized(
                                MessageResponse(
                                    call.userId.toString(),
                                    newMessageRequest.content,
                                    Clock.System.now(),
                                    MessageType.USER
                                )
                            )
                            val offlineUsers = mutableListOf<UserResponse>()
                            chat.users.forEach {
                                if (!serverRooms[roomIndex].connectedSessions.map { session.call.userId }
                                        .contains(ObjectId(it.id))) {
                                    offlineUsers.add(it)
                                }
                            }
                            // TODO: send notification to offline users
                        }
                    }
                } // once socket is closed, the loop will complete
                if (serverRooms[roomIndex].connectedSessions.size > 1) {
                    serverRooms[roomIndex].connectedSessions.remove(this)
                } else {
                    serverRooms.removeAt(roomIndex)
                }
                // TODO: there's a bug where the client disconnects from the socket without closing the receive channel, in this case the room is never cleared.
            }
        }
    }
}