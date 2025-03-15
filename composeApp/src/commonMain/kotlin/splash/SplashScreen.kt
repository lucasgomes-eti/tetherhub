package splash

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents

object SplashScreen : Screen {
    @Composable
    override fun Content() {
        val splashScreenModel = koinScreenModel<SplashScreenModel>()
        ObserveNavigationEvents(splashScreenModel.navigationActions)
    }

}