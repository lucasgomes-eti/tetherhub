package splash

import DataStoreKeys
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import auth.login.LoginScreen
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dsl.navigation.NavigationAction
import home.HomeScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashScreenModel(private val preferences: DataStore<Preferences>) : ScreenModel {
    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        getRootDestination()
    }

    private fun getRootDestination() {
        screenModelScope.launch {
            val userIsPersisted =
                preferences.data.map { it[stringPreferencesKey(DataStoreKeys.USER_ID)] }
                    .firstOrNull()
                    .let { it != null }

            _navigationActions.send(
                NavigationAction.Replace(
                    if (userIsPersisted) HomeScreen else LoginScreen
                )
            )
        }
    }
}