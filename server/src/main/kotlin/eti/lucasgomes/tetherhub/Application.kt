package eti.lucasgomes.tetherhub

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mongodb.kotlin.client.coroutine.MongoClient
import eti.lucasgomes.tetherhub.user.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) { gson { } }

    install(Koin) {
        slf4jLogger()
        modules(
            module { // Database module
                single {
                    MongoClient.create(
                        environment.config.propertyOrNull("ktor.mongo.uri")?.getString()
                            ?: throw RuntimeException("Failed to access MongoDB URI.")
                    )
                }
                single {
                    get<MongoClient>().getDatabase(
                        environment.config.property("ktor.mongo.database").getString()
                    )
                }
            },
            module {// Repository module
                single { UserRepository(get()) }
            },
            module { // Mapper module
                single { UserMapper(get()) }
            },
            module { // Service module
                single { UserService(get(), get()) }
            }
        )
    }

    val realm =
        environment.config.propertyOrNull("ktor.auth.realm")?.getString() ?: throw RuntimeException(
            "Realm not set"
        )
    val secret = environment.config.propertyOrNull("ktor.auth.secret")?.getString()
        ?: throw RuntimeException("Secret not set")
    val issuer = environment.config.propertyOrNull("ktor.auth.issuer")?.getString()
        ?: throw RuntimeException("Issuer not set")

    val userRepository by inject<UserRepository>()

    install(Authentication) {
        jwt {
            this.realm = realm
            verifier(JWT.require(Algorithm.HMAC256(secret)).withIssuer(issuer).build())
            validate { jwtCredential ->
                jwtCredential.payload.getClaim("email").asString()
                    ?.let { userRepository.findUserByEmail(it)?.toDomain() }
            }
        }
    }

    routing {
        swaggerUI(path = "swagger-ui", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
        userRoutes()
        authenticate {
            route("feed") {
                get {
                    call.respond(
                        listOf<String>(
//                            Post(
//                                "1",
//                                User("1", "scary"),
//                                "We are currently aware of an issue that these balance changes are only reflected in a game mode that appears in the arcade. So we’re gonna call it “Balanced Overwatch” for now. Sorry for any confusion.",
//                                3
//                            ), Post(
//                                "2",
//                                User("1", "scary"),
//                                "There are also some new challenges that are granting some of our developer’s doodles as sprays. We’re not sure why that is happening, but they are really cool looking.",
//                                0
//                            )
                        )
                    )
                }
            }
        }
    }
}