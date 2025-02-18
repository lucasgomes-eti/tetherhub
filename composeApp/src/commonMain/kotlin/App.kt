import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import auth.login.LoginScreen
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import home.HomeScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import navigation.NavigationAction
import navigation.ObserveNavigationEvents
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val scope = rememberCoroutineScope()
            val eventBus = koinInject<EventBus>()
            CompositionLocalProvider(LocalEventBus provides eventBus) {
                Navigator(SplashScreen)
            }
        } // needs to initialize a splash screen and check if the user is remembered
    }
}

object SplashScreen : Screen {
    @Composable
    override fun Content() {
        val splashScreenModel = koinScreenModel<SplashScreenModel>()
        ObserveNavigationEvents(splashScreenModel.navigationActions)
    }

}

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

internal val LocalEventBus = compositionLocalOf<EventBus> { error("No EventBus provided") }

class EventBus {
    val events = MutableSharedFlow<Event>()

    suspend fun publish(event: Event) {
        events.emit(event)
    }

    suspend inline fun <reified T : Event> subscribe(crossinline block: (T) -> Unit) {
        events.filterIsInstance<T>().collect { block(it) }
    }
}

interface Event