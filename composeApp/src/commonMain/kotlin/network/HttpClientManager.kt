package network

import DataStoreKeys
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import auth.RegisterFcmToken
import dsl.eventbus.EventBus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import request.RefreshTokenRequest
import response.AuthResponse
import response.TetherHubError
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

class HttpClientManager(
    private val engine: HttpClientEngine,
    private val preferences: DataStore<Preferences>,
    val baseUrl: BaseUrl,
    private val eventBus: EventBus
) {
    private var _httpClient: HttpClient? = null

    suspend fun getClient(): HttpClient {
        return _httpClient ?: createHttpClient().also { _httpClient = it }
    }

    private suspend fun createHttpClient(): HttpClient {
        val userIsPersisted =
            preferences.data.map { it[stringPreferencesKey(DataStoreKeys.USER_ID)] }.firstOrNull()
                .let { it != null }
        return HttpClient(engine) {
            val contentSerializer = Json {
                ignoreUnknownKeys = true
            }
            install(Logging) { // TODO: Setup multiplatform logger for debug builds (Kermit?)
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    json = contentSerializer
                )
            }
            defaultRequest {
                url(baseUrl.path)
            }

            if (userIsPersisted) {
                install(Auth) {
                    bearer {
                        loadTokens {
                            // Load tokens from a local storage and return them as the 'BearerTokens' instance
                            val token =
                                preferences.data.map { it[stringPreferencesKey(DataStoreKeys.TOKEN)] }
                                    .firstOrNull() ?: ""
                            val refreshToken =
                                preferences.data.map { it[stringPreferencesKey(DataStoreKeys.REFRESH_TOKEN)] }
                                    .firstOrNull() ?: ""
                            BearerTokens(token, refreshToken)
                        }
                        refreshTokens {
                            // Refresh tokens and return them as the 'BearerTokens' instance
                            val response = client.post("users/login/refresh") {
                                contentType(ContentType.Application.Json)
                                setBody(RefreshTokenRequest(oldTokens?.refreshToken ?: ""))
                            }
                            val auth = when (response.status.value) {
                                in 200..299 -> {
                                    response.body<AuthResponse>().also { auth ->
                                        preferences.edit { dataStore ->
                                            dataStore[stringPreferencesKey(DataStoreKeys.USER_ID)] =
                                                auth.userId
                                            dataStore[stringPreferencesKey(DataStoreKeys.TOKEN)] =
                                                auth.token
                                            dataStore[stringPreferencesKey(DataStoreKeys.REFRESH_TOKEN)] =
                                                auth.refreshToken
                                        }
                                        eventBus.publish(RegisterFcmToken)
                                    }
                                }

                                else -> {
                                    logOut()
                                    null
                                }
                            }

                            auth?.let {
                                BearerTokens(it.token, it.refreshToken)
                            }
                        }
                    }
                }
            }

            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(contentSerializer)
                pingInterval = 20.seconds.inWholeMilliseconds
                maxFrameSize = Long.MAX_VALUE
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10.seconds.inWholeMilliseconds
            }
        }
    }

    suspend fun logOut() {
        preferences.edit { dataStore ->
            dataStore.remove(stringPreferencesKey(DataStoreKeys.USER_ID))
            dataStore.remove(stringPreferencesKey(DataStoreKeys.TOKEN))
            dataStore.remove(stringPreferencesKey(DataStoreKeys.REFRESH_TOKEN))
        }
        eventBus.publish(Logout)
    }

    suspend fun installAuth() {
        refreshHttpClient()
    }

    private suspend fun refreshHttpClient() {
        _httpClient?.close()
        _httpClient = createHttpClient()
    }

    suspend inline fun <reified T> withApiResource(
        noinline onSuccess: (suspend (T) -> Unit)? = null,
        noinline onFailure: (suspend (TetherHubError) -> Unit)? = null,
        httpRequest: HttpClient.() -> HttpResponse
    ): Resource<T> {
        val response = try {
            getClient().httpRequest()
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Resource.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<T>()
                onSuccess?.invoke(data)
                Resource.Success(data)
            }

            in 400..599 -> {
                val error = try {
                    response.body<TetherHubError>()
                } catch (_: Exception) {
                    unexpectedErrorWithHttpStatusCode(response.status.value)
                }
                onFailure?.invoke(error)
                Resource.Error(error)
            }

            else -> {
                val error = unexpectedErrorWithHttpStatusCode(response.status.value)
                onFailure?.invoke(error)
                Resource.Error(error)
            }
        }
    }
}

