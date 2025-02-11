package eti.lucasgomes.tetherhub.chat

import Message
import eti.lucasgomes.tetherhub.dsl.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import io.ktor.websocket.send
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject
import request.CreateChatRequest
import response.CreateChatResponse
import java.util.Collections

data class ServerRooms(
    val chat: CreateChatResponse,
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
                call.respond(HttpStatusCode.fromValue(it.httCode), it)
            }.onRight { call.respond(HttpStatusCode.Created, it) }
        }

        get {
            chatService.findRoomsByUserId(userId).onLeft {
                call.respond(HttpStatusCode.fromValue(it.httCode), it)
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
                send("You joined the room: ${chat.roomName}")

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

                send("Online members: ${serverRooms[roomIndex].connectedSessions.size}")

                while (true) {
                    val newMessage = receiveDeserialized<Message>()
                    for (session in serverRooms[roomIndex].connectedSessions) {
                        session.sendSerialized(newMessage)

                        val offlineUsers = mutableListOf<String>()
                        chat.users.forEach {
                            if (!serverRooms[roomIndex].connectedSessions.map { session.call.userId }
                                    .contains(ObjectId(it))) {
                                offlineUsers.add(it)
                            }
                        }
                        // TODO: send notification to offline users
                    }
                }
            }
        }
    }
}