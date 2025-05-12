package splash

import DataStoreKeys
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import auth.login.LoginScreen
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import home.HomeScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

class SplashScreenModel(
    private val preferences: DataStore<Preferences>
) : ScreenModel {
    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        navigateToRoot()
    }

    private suspend fun userIsPersisted(): Boolean {
        return preferences.data.map { it[stringPreferencesKey(DataStoreKeys.USER_ID)] }
            .firstOrNull()
            .let { it != null }
    }

    private suspend fun getRootDestination(): Screen {
        return if (userIsPersisted()) HomeScreen else LoginScreen
    }

    private fun navigateToRoot() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Replace(getRootDestination()))
    }
}