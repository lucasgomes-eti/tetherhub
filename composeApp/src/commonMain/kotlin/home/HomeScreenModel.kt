package home

import DataStoreKeys
import FcmTokenManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import auth.RegisterFcmToken
import auth.login.LoginScreen
import auth.registration.RegistrationClient
import cafe.adriel.voyager.core.model.ScreenModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import dsl.eventbus.EventBus
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import network.Logout
import network.onSuccess
import request.FcmTokenRequest

class HomeScreenModel(
    private val preferences: DataStore<Preferences>,
    private val registrationClient: RegistrationClient,
    private val permissionsController: PermissionsController,
    private val eventBus: EventBus
) : ScreenModel {

    init {
        askNotificationPermissionConditionally()
        subscribeToLogout()
        subscribeToRegisterFcmToken()
    }

    private fun subscribeToRegisterFcmToken() = withScreenModelScope {
        eventBus.subscribe<RegisterFcmToken> {
            registerFcmToken()
        }
    }

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    private fun subscribeToLogout() = withScreenModelScope {
        eventBus.subscribe<Logout> {
            _navigationActions.send(NavigationAction.Replace(LoginScreen))
        }
    }

    private fun askNotificationPermissionConditionally() = withScreenModelScope {
        if (permissionsController.isPermissionGranted(Permission.REMOTE_NOTIFICATION).not()) {
            try {
                permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
            } catch (_: DeniedAlwaysException) {
            } catch (_: DeniedException) {
            }
        }
    }

    fun verifyFcmToken() = withScreenModelScope {
        if (userIsPersisted().not()) {
            return@withScreenModelScope
        }
        if (isFcmTokenSet().not()) {
            registerFcmToken()
        }
    }

    private suspend fun registerFcmToken() {
        registrationClient.registerFcmTokenForUser(FcmTokenRequest(FcmTokenManager.getFcmToken()))
            .onSuccess {
                setFcmToken(true)
            }
    }

    private suspend fun userIsPersisted(): Boolean {
        return preferences.data.map { it[stringPreferencesKey(DataStoreKeys.USER_ID)] }
            .firstOrNull()
            .let { it != null }
    }

    private suspend fun isFcmTokenSet(): Boolean {
        return preferences.data.map { it[booleanPreferencesKey(DataStoreKeys.IS_FCM_TOKEN_SET)] }
            .firstOrNull() ?: false
    }

    private suspend fun setFcmToken(value: Boolean) {
        preferences.edit { it[booleanPreferencesKey(DataStoreKeys.IS_FCM_TOKEN_SET)] = value }
    }
}