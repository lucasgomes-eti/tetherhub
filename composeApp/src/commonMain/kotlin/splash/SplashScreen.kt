package splash

import DeepLink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents
import org.koin.core.parameter.parametersOf

data class SplashScreen(val deepLink: DeepLink? = null) : Screen {
    @Composable
    override fun Content() {
        val splashScreenModel = koinScreenModel<SplashScreenModel> { parametersOf(deepLink) }
        LaunchedEffect(Unit) {
            splashScreenModel.navigateToRoot()
        }
        ObserveNavigationEvents(splashScreenModel.navigationActions)
    }
}