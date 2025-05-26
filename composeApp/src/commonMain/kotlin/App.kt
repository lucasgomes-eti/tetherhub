import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dsl.eventbus.EventBus
import dsl.eventbus.LocalEventBus
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import splash.SplashScreen

@Composable
@Preview
fun App(deepLink: DeepLink? = null) {
    MaterialTheme {
        KoinContext {
            val eventBus = koinInject<EventBus>()
            val permissionsController = koinInject<PermissionsController>()
            BindEffect(permissionsController)
            CompositionLocalProvider(LocalEventBus provides eventBus) {
                Navigator(SplashScreen(deepLink))
            }
        }
    }
}