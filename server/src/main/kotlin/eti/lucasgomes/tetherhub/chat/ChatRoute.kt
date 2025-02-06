package eti.lucasgomes.tetherhub.chat

import Message
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.send
import org.koin.ktor.ext.inject
import request.CreateChatRequest
import java.util.Collections

fun Route.chatRoutes() {
    val chatService by inject<ChatService>()
    route("chats") {
        val sessions = Collections.synchronizedList<WebSocketServerSession>(ArrayList())

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

        webSocket("{chatId}") {
            sessions.add(this)

            send(call.parameters["chatId"] ?: "no id provided")

            while (true) {
                val newMessage = receiveDeserialized<Message>()
                for (session in sessions) {
                    session.sendSerialized(newMessage)
                }
            }
        }
    }
}