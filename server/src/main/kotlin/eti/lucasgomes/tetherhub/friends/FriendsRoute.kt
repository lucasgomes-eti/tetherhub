package eti.lucasgomes.tetherhub.friends

import eti.lucasgomes.tetherhub.dsl.getParameterAsObjectIdOrRespond
import eti.lucasgomes.tetherhub.dsl.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import request.FriendshipSolicitationRequest

fun Route.friendsRoutes() {
    val friendsService: FriendsService by inject()
    route("friends") {
        get {
            friendsService.getFriendsByUser(userId).onLeft {
                call.respond(HttpStatusCode.fromValue(it.httpCode), it)
            }.onRight {
                call.respond(it)
            }
        }
        get("requests") {
            friendsService.getFriendshipRequestsByUser(userId).onLeft {
                call.respond(HttpStatusCode.fromValue(it.httpCode), it)
            }.onRight {
                call.respond(it)
            }
        }
        post("requests") {
            val request = try {
                call.receive<FriendshipSolicitationRequest>()
            } catch (e: Exception) {
                call.respond(status = HttpStatusCode.BadRequest, FriendsErrors.InvalidParameters)
                return@post
            }
            friendsService.createFriendshipSolicitation(request, userId).onLeft {
                call.respond(HttpStatusCode.fromValue(it.httpCode), it)
            }.onRight { call.respond(HttpStatusCode.Created) }
        }
        post("requests/{requestId}/accept") {
            getParameterAsObjectIdOrRespond("requestId") {
                respond(HttpStatusCode.BadRequest, FriendsErrors.InvalidParameters)
            }.onRight { requestId ->
                friendsService.acceptFriendRequest(
                    friendshipRequestId = requestId,
                    clientUserId = userId
                ).onLeft { call.respond(HttpStatusCode.fromValue(it.httpCode), it) }.onRight {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}