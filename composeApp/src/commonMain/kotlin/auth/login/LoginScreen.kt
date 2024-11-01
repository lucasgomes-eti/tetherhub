package auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen

object LoginScreen : Screen {

    @Composable
    override fun Content() {
        val loginScreenModel = rememberScreenModel { LoginScreenModel() }
        val loginUiState by loginScreenModel.uiState.collectAsState()
        Login(loginUiState, loginScreenModel::onAction)
    }

}