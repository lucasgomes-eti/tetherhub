package network

import DataStoreKeys
import FcmTokenManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dsl.eventbus.EventBus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
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
import request.FcmTokenRequest
import request.RefreshTokenRequest
import response.AuthResponse
import response.TetherHubError
import kotlin.coroutines.cancellation.CancellationException

class HttpClientManager(
    private val engine: HttpClientEngine,
    private val preferences: DataStore<Preferences>,
    val baseUrl: BaseUrl,
    private val eventBus: EventBus
) {
    private var _httpClient: HttpClient? = null

    suspend fun getClient(): HttpClient {
        if (_httpClient == null) {
            _httpClient = createHttpClient()
        }
        return _httpClient!!
    }

    private suspend fun createHttpClient(): HttpClient {
        val userIsPersisted =
            preferences.data.map { it[stringPreferencesKey(DataStoreKeys.USER_ID)] }.firstOrNull()
                .let { it != null }
        return HttpClient(engine) {
            val contentSerializer = Json {
                ignoreUnknownKeys = true
            }
            install(Logging) {
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
                            return@refreshTokens when (response.status.value) {
                                in 200..299 -> {
                                    val auth = response.body<AuthResponse>()
                                    preferences.edit { dataStore ->
                                        dataStore[stringPreferencesKey(DataStoreKeys.USER_ID)] =
                                            auth.userId
                                        dataStore[stringPreferencesKey(DataStoreKeys.TOKEN)] =
                                            auth.token
                                        dataStore[stringPreferencesKey(DataStoreKeys.REFRESH_TOKEN)] =
                                            auth.refreshToken
                                    }

                                    try {
                                        client.post("users/register_fcm_token") {
                                            headers.append("Authorization", "Bearer ${auth.token}")
                                            contentType(ContentType.Application.Json)
                                            setBody(FcmTokenRequest(FcmTokenManager.getFcmToken()))
                                        }
                                    } catch (_: Exception) {
                                    }

                                    BearerTokens(auth.token, auth.refreshToken)
                                }

                                else -> {
                                    logOut()
                                    null
                                }
                            }

                        }
                    }
                }
            }

            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(contentSerializer)
            }
        }
    }

    private suspend fun logOut() {
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
                val error = response.body<TetherHubError>()
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

