package eti.lucasgomes.tetherhub

import Greeting
import SERVER_PORT
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import model.Post
import model.User
import java.util.Date

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    install(CallLogging)
    install(ContentNegotiation) { gson { } }

    val userSource = UserSource()

    val myRealm = "tetherhub"
    val secret = "secret"
    val issuer = "http://0.0.0.0:8080/"

    install(Authentication) {
        jwt {
            realm = myRealm
            verifier(JWT.require(Algorithm.HMAC256(secret)).withIssuer(issuer).build())
            validate { jwtCredential ->
                jwtCredential.payload.getClaim("username").asString()?.let(userSource::findUserByUsername)
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        post("login") {
            val credentials = call.receive<UserPasswordCredential>()
            userSource.findUserByCredentials(credentials)?.let { user ->
                val token = JWT.create().withIssuer(issuer).withClaim("username", user.username)
                    .withExpiresAt(
                        Date(System.currentTimeMillis() + 86400000)
                    ).sign(Algorithm.HMAC256(secret))
                call.respond(hashMapOf("token" to token))
            } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid credentials")
        }
        authenticate {
            route("feed") {
                get {
                    call.respond(
                        listOf(
                            Post(
                                "1",
                                User("1", "scary"),
                                "We are currently aware of an issue that these balance changes are only reflected in a game mode that appears in the arcade. So we’re gonna call it “Balanced Overwatch” for now. Sorry for any confusion.",
                                3
                            ), Post(
                                "2",
                                User("1", "scary"),
                                "There are also some new challenges that are granting some of our developer’s doodles as sprays. We’re not sure why that is happening, but they are really cool looking.",
                                0
                            )
                        )
                    )
                }
            }
        }
    }
}