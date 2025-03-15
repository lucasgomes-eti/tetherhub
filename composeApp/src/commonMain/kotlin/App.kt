import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.navigator.Navigator
import dsl.eventbus.EventBus
import dsl.eventbus.LocalEventBus
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import splash.SplashScreen

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
        }
    }
}