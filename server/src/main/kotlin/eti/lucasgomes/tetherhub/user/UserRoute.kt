package eti.lucasgomes.tetherhub.user

import arrow.core.Either
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import request.CreateUserRequest
import java.util.*

fun Route.userRoutes() {
    val userMapper by inject<UserMapper>()
    val userService by inject<UserService>()
    val secret = environment?.config?.propertyOrNull("ktor.auth.secret")?.getString()
        ?: throw RuntimeException("Secret not set")
    val issuer = environment?.config?.propertyOrNull("ktor.auth.issuer")?.getString()
        ?: throw RuntimeException("Issuer not set")
    val expiration = environment?.config?.propertyOrNull("ktor.auth.expiration")?.getString()
        ?: throw RuntimeException("Issuer not set")

    post("user") {
        val createUserRequest = try {
            call.receive<CreateUserRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, UserErrors.InvalidParameters)
            return@post
        }
        when (val result = userService.saveUser(createUserRequest)) {
            is Either.Left -> call.respond(
                HttpStatusCode.fromValue(result.value.httCode),
                result.value
            )

            is Either.Right -> call.respond(HttpStatusCode.Created, result.value)
        }
    }

    post("login") {
        val credentials = call.receive<EmailPasswordCredentials>()
        when (val result = userService.findUserByCredentials(credentials)) {
            is Either.Left -> {
                call.respond(status = HttpStatusCode.BadRequest, message = result.value.message)
            }

            is Either.Right -> {
                val expiresAt = System.currentTimeMillis() + expiration.toInt()
                val token = JWT.create().withIssuer(issuer).withClaim("email", result.value.email.value)
                    .withExpiresAt(
                        Date(expiresAt)
                    ).sign(Algorithm.HMAC256(secret))
                call.respond(hashMapOf("token" to token, "expiration" to expiresAt))
            }
        }
    }
}