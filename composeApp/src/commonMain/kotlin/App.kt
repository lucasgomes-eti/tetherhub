import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import auth.login.LoginScreen
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
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
                Navigator(LoginScreen)
            }
        } // needs to initialize a splash screen and check if the user is remembered
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