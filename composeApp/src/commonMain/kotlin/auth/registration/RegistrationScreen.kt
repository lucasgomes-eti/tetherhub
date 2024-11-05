package auth.registration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import auth.registration.network.RegistrationClient
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen

data class RegistrationScreen(val registrationClient: RegistrationClient) : Screen {

    @Composable
    override fun Content() {
        val registrationScreenModel =
            rememberScreenModel { RegistrationScreenModel(registrationClient) }
        val registrationUiState by registrationScreenModel.uiState.collectAsState()
        Registration(registrationUiState, registrationScreenModel::onAction)
    }
}