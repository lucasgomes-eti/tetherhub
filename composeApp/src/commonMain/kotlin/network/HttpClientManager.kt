package network

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
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import response.TetherHubError
import kotlin.coroutines.cancellation.CancellationException

class HttpClientManager(private val engine: HttpClientEngine, private val baseUrl: String) {
    private var _httpClient: HttpClient? = null

    // TODO: token needs to be saved in the local storage
    private var authToken: String? = null

    val client: HttpClient
        get() {
            if (_httpClient == null) {
                _httpClient = createHttpClient()
            }
            return _httpClient!!
        }

    private fun createHttpClient(): HttpClient {
        return HttpClient(engine) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                url(baseUrl)
            }

            authToken?.let { token ->
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(token, "")
                        }
                    }
                }
            }
        }
    }

    fun installAuth(token: String) {
        authToken = token
        refreshHttpClient()
    }

    private fun refreshHttpClient() {
        _httpClient?.close()
        _httpClient = createHttpClient()
    }

    suspend inline fun <reified T> withApiResource(httpRequest: HttpClient.() -> HttpResponse): Resource<T> {
        val response = try {
            client.httpRequest()
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            return Resource.Error(unexpectedErrorWithException(e))
        }

        return when (response.status.value) {
            in 200..299 -> {
                Resource.Success(response.body<T>())
            }

            in 400..599 -> {
                Resource.Error(response.body<TetherHubError>())
            }

            else -> Resource.Error(unexpectedErrorWithHttpStatusCode(response.status.value))
        }
    }
}

