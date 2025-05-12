package home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

object HomeScreen : Screen {

    @Composable
    override fun Content() {
        koinScreenModel<HomeScreenModel>()
        Home()
    }
}