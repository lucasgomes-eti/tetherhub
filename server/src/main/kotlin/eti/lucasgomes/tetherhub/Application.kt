package eti.lucasgomes.tetherhub

import TERMS_AND_PRIVACY_PATH
import THIRD_PARTY_SOFTWARE_PATH
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.mongodb.kotlin.client.coroutine.MongoClient
import eti.lucasgomes.tetherhub.chat.ChatMapper
import eti.lucasgomes.tetherhub.chat.ChatRepository
import eti.lucasgomes.tetherhub.chat.ChatService
import eti.lucasgomes.tetherhub.chat.chatRoutes
import eti.lucasgomes.tetherhub.friends.FriendsMapper
import eti.lucasgomes.tetherhub.friends.FriendsRepository
import eti.lucasgomes.tetherhub.friends.FriendsService
import eti.lucasgomes.tetherhub.friends.friendsRoutes
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
import eti.lucasgomes.tetherhub.user.userConfigRoutes
import eti.lucasgomes.tetherhub.user.userRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import response.TetherHubError
import java.io.FileInputStream
import java.time.Duration

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
                singleOf(::ChatRepository)
                singleOf(::FriendsRepository)
            },
            module { // Mapper module
                single { UserMapper(get()) }
                singleOf(::ProfileMapper)
                single { PostMapper() }
                singleOf(::ChatMapper)
                singleOf(::FriendsMapper)
            },
            module { // Service module
                single { UserService(get(), get()) }
                single { ProfileService(get(), get()) }
                single { PostService(get(), get(), get()) }
                singleOf(::ChatService)
                singleOf(::FriendsService)
                single<FirebaseApp> {
                    val serviceAccount = FileInputStream("firebase-admin-service-account-key.json")
                    val options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build()
                    FirebaseApp.initializeApp(options)
                }
                single<FirebaseMessaging> { FirebaseMessaging.getInstance(get()) }
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
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    TetherHubError(401, "TH-010", "Authentication expired")
                )
            }
        }
    }

    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        swaggerUI(path = "swagger-ui", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
        staticResources(TERMS_AND_PRIVACY_PATH, "static", index = "terms_and_privacy.html")
        staticResources(THIRD_PARTY_SOFTWARE_PATH, "static", index = "third_party_software.html")
        userRoutes()
        authenticate {
            profileRoutes()
            postRoutes()
            chatRoutes()
            friendsRoutes()
            userConfigRoutes()
        }
    }
}