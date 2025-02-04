package eti.lucasgomes.tetherhub.chat

import Message
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.send
import java.util.Collections

fun Route.chatRoutes() {
    route("chats") {
        val sessions = Collections.synchronizedList<WebSocketServerSession>(ArrayList())

        post {

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