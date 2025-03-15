package eti.lucasgomes.tetherhub.profile

import eti.lucasgomes.tetherhub.dsl.userEmail
import eti.lucasgomes.tetherhub.dsl.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.profileRoutes() {
    val profileService by inject<ProfileService>()

    route("profiles") {
        get("my_profile") {
            profileService.getProfile(userEmail).onLeft {
                call.respond(status = HttpStatusCode.fromValue(it.httpCode), message = it)
            }.onRight { call.respond(it) }
        }

        get {
            val usernameFilter = call.request.queryParameters["username"]
            if (usernameFilter == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ProfileErrors.MissingUsernameQueryFilter
                )
                return@get
            }
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            val size = call.request.queryParameters["size"]?.toInt() ?: 20
            profileService.getProfilesByUsername(usernameFilter, userId, page, size).onLeft {
                call.respond(HttpStatusCode.fromValue(it.httpCode), it)
            }.onRight { call.respond(it) }
        }
    }
}