package auth.registration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

object RegistrationScreen : Screen {

    @Composable
    override fun Content() {
        val registrationScreenModel = koinScreenModel<RegistrationScreenModel>()
        val registrationUiState by registrationScreenModel.uiState.collectAsState()
        Registration(registrationUiState, registrationScreenModel::onAction)
    }
}