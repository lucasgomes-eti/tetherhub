package auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents

object LoginScreen : Screen {

    @Composable
    override fun Content() {
        val loginScreenModel = koinScreenModel<LoginScreenModel>()
        val loginUiState by loginScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(loginScreenModel.navigationActions)
        Login(loginUiState, loginScreenModel::onAction)
    }

}