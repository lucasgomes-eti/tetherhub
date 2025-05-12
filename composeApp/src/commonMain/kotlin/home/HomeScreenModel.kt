package home

import DataStoreKeys
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import auth.registration.network.RegistrationClient
import cafe.adriel.voyager.core.model.ScreenModel
import com.mmk.kmpnotifier.notification.NotifierManager
import dsl.withScreenModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import network.onError
import network.onSuccess
import request.FcmTokenRequest

class HomeScreenModel(
    private val preferences: DataStore<Preferences>,
    private val registrationClient: RegistrationClient
) : ScreenModel {

    private var notificationsListener: NotifierManager.Listener? = null

    init {
        verifyFcmToken()
        notificationsListener = provideNotificationsListener()
        NotifierManager.setListener(notificationsListener)
    }

    override fun onDispose() {
        notificationsListener = null
        super.onDispose()
    }

    private fun provideNotificationsListener(): NotifierManager.Listener {
        return object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                handleNewToken(token)
            }
        }
    }

    private fun handleNewToken(token: String) = withScreenModelScope {
        registrationClient.registerFcmTokenForUser(FcmTokenRequest(token)).onError {
            setFcmToken(false)
        }
    }

    private suspend fun userIsPersisted(): Boolean {
        return preferences.data.map { it[stringPreferencesKey(DataStoreKeys.USER_ID)] }
            .firstOrNull()
            .let { it != null }
    }

    private fun verifyFcmToken() = withScreenModelScope {
        if (userIsPersisted().not()) {
            return@withScreenModelScope
        }
        if (isFcmTokenSet().not()) {
            val token = NotifierManager.getPushNotifier().getToken() ?: return@withScreenModelScope
            registrationClient.registerFcmTokenForUser(FcmTokenRequest(token)).onSuccess {
                setFcmToken(true)
            }
        }
    }

    private suspend fun isFcmTokenSet(): Boolean {
        return preferences.data.map { it[booleanPreferencesKey(DataStoreKeys.IS_FCM_TOKEN_SET)] }
            .firstOrNull() ?: false
    }

    private suspend fun setFcmToken(value: Boolean) {
        preferences.edit { it[booleanPreferencesKey(DataStoreKeys.IS_FCM_TOKEN_SET)] = value }
    }
}