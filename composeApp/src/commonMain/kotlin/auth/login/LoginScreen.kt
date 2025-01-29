package auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

object LoginScreen : Screen {

    @Composable
    override fun Content() {
        val loginScreenModel = koinScreenModel<LoginScreenModel>()
        val loginUiState by loginScreenModel.uiState.collectAsState()
        Login(loginUiState, loginScreenModel::onAction)
    }

}