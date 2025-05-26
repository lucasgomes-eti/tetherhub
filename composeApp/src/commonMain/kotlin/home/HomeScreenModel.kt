package home

import DataStoreKeys
import FcmTokenManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import auth.registration.network.RegistrationClient
import cafe.adriel.voyager.core.model.ScreenModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import dsl.withScreenModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import network.onSuccess
import request.FcmTokenRequest

class HomeScreenModel(
    private val preferences: DataStore<Preferences>,
    private val registrationClient: RegistrationClient,
    private val permissionsController: PermissionsController
) : ScreenModel {

    init {
        askNotificationPermissionConditionally()
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
            registrationClient.registerFcmTokenForUser(FcmTokenRequest(FcmTokenManager.getFcmToken()))
                .onSuccess {
                    setFcmToken(true)
                }
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