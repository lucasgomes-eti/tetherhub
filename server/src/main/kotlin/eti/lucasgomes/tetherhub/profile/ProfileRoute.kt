package eti.lucasgomes.tetherhub.profile

import arrow.core.Either
import eti.lucasgomes.tetherhub.userEmail
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.profileRoutes() {
    val profileService by inject<ProfileService>()

    route("profile") {
        get {
            when (val result = profileService.getProfile(userEmail)) {
                is Either.Left -> {
                    call.respond(status = HttpStatusCode.BadRequest, message = result.value.message)
                }

                is Either.Right -> {
                    call.respond(result.value)
                }
            }
        }
    }
}