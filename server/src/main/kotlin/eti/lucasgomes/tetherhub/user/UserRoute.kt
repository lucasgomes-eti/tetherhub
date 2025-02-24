package eti.lucasgomes.tetherhub.user

import arrow.core.Either
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import request.CreateUserRequest
import request.RefreshTokenRequest
import response.AuthResponse
import java.util.Date

fun Route.userRoutes() {
    val userService by inject<UserService>()
    val secret = environment?.config?.propertyOrNull("ktor.auth.secret")?.getString()
        ?: throw RuntimeException("Secret not set")
    val issuer = environment?.config?.propertyOrNull("ktor.auth.issuer")?.getString()
        ?: throw RuntimeException("Issuer not set")
    val expiration = environment?.config?.propertyOrNull("ktor.auth.expiration")?.getString()
        ?: throw RuntimeException("Issuer not set")

    route("users") {
        post {
            val createUserRequest = try {
                call.receive<CreateUserRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, UserErrors.InvalidParameters)
                return@post
            }
            when (val result = userService.saveUser(createUserRequest)) {
                is Either.Left -> call.respond(
                    HttpStatusCode.fromValue(result.value.httpCode),
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
                    val tokenExpiresAt = System.currentTimeMillis() + expiration.toInt()
                    val refreshTokenExpiresAt = tokenExpiresAt * 15
                    val token = JWT.create()
                        .withIssuer(issuer)
                        .withClaim("email", result.value.email.value)
                        .withClaim("user_id", result.value.id.value.toString())
                        .withExpiresAt(Date(tokenExpiresAt))
                        .sign(Algorithm.HMAC256(secret))
                    val refreshToken = JWT.create()
                        .withIssuer(issuer)
                        .withClaim("email", result.value.email.value)
                        .withClaim("user_id", result.value.id.value.toString())
                        .withClaim("refresh", true)
                        .withExpiresAt(Date(refreshTokenExpiresAt))
                        .sign(Algorithm.HMAC256(secret))
                    call.respond(
                        AuthResponse(
                            userId = result.value.id.value.toString(),
                            token = token,
                            refreshToken = refreshToken
                        )
                    )
                }
            }
        }

        post("login/refresh") {
            val refreshToken = call.receive<RefreshTokenRequest>()
            val verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .withClaim("refresh", true)
                .build()
            val token = verifier.verify(refreshToken.value)
            val userId = token.getClaim("user_id").asString()
            val email = token.getClaim("email").asString()

            val tokenExpiresAt = System.currentTimeMillis() + expiration.toInt()
            val refreshTokenExpiresAt = System.currentTimeMillis() + (expiration.toInt() * 15)
            val newToken = JWT.create()
                .withIssuer(issuer)
                .withClaim("email", email)
                .withClaim("user_id", userId)
                .withExpiresAt(Date(tokenExpiresAt))
                .sign(Algorithm.HMAC256(secret))
            val newRefreshToken = JWT.create()
                .withIssuer(issuer)
                .withClaim("email", email)
                .withClaim("user_id", userId)
                .withClaim("refresh", true)
                .withExpiresAt(Date(refreshTokenExpiresAt))
                .sign(Algorithm.HMAC256(secret))
            call.respond(
                AuthResponse(
                    userId = userId,
                    token = newToken,
                    refreshToken = newRefreshToken
                )
            )
        }
    }
}