package auth.login

import DataStoreKeys
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import network.HttpClientManager
import network.Resource
import response.AuthResponse

class LoginClient(
    private val httpClientManager: HttpClientManager,
    private val preferences: DataStore<Preferences>
) {
    suspend fun authenticateWithCredentials(
        email: String,
        password: String
    ): Resource<AuthResponse> = httpClientManager.withApiResource(onSuccess = ::createUserSession) {
        post("/login") {
            contentType(ContentType.Application.Json)
            setBody(hashMapOf("email" to email, "password" to password))
        }
    }

    private suspend fun createUserSession(auth: AuthResponse) {
        httpClientManager.installAuth(auth.token)
        preferences.edit { dataStore ->
            dataStore[stringPreferencesKey(DataStoreKeys.USER_ID)] = auth.userId
            dataStore[stringPreferencesKey(DataStoreKeys.TOKEN)] = auth.token
            dataStore[longPreferencesKey(DataStoreKeys.TOKEN_EXPIRATION)] = auth.expiresAt
        }
    }
}