package eti.lucasgomes.tetherhub

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mongodb.kotlin.client.coroutine.MongoClient
import eti.lucasgomes.tetherhub.post.PostMapper
import eti.lucasgomes.tetherhub.post.PostRepository
import eti.lucasgomes.tetherhub.post.PostService
import eti.lucasgomes.tetherhub.post.postRoutes
import eti.lucasgomes.tetherhub.profile.ProfileMapper
import eti.lucasgomes.tetherhub.profile.ProfileService
import eti.lucasgomes.tetherhub.profile.profileRoutes
import eti.lucasgomes.tetherhub.user.UserMapper
import eti.lucasgomes.tetherhub.user.UserRepository
import eti.lucasgomes.tetherhub.user.UserService
import eti.lucasgomes.tetherhub.user.toDomain
import eti.lucasgomes.tetherhub.user.userRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) { json() }

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
                single { PostRepository(get()) }
            },
            module { // Mapper module
                single { UserMapper(get()) }
                single { ProfileMapper() }
                single { PostMapper() }
            },
            module { // Service module
                single { UserService(get(), get()) }
                single { ProfileService(get(), get()) }
                single { PostService(get(), get(), get()) }
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

    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ApplicationErrors.Unauthorized(call.request.uri)
            )
        }
    }

    routing {
        swaggerUI(path = "swagger-ui", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
        userRoutes()
        authenticate {
            profileRoutes()
            postRoutes()
        }
    }
}