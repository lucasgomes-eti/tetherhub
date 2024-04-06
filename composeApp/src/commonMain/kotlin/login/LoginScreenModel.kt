package login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            email = "",
            password = "",
            errorMsg = "",
            isLoading = false,
            event = LoginEvent.NONE
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(loginAction: LoginAction) {
        when (loginAction) {
            is LoginAction.EmailChanged -> onEmailChanged(loginAction.value)
            is LoginAction.PasswordChanged -> onPasswordChanged(loginAction.value)
            is LoginAction.Login -> onLogin()
        }
    }

    private fun onEmailChanged(value: String) {
        _uiState.update { state -> state.copy(email = value, errorMsg = "") }
    }

    private fun onPasswordChanged(value: String) {
        _uiState.update { state -> state.copy(password = value, errorMsg = "") }
    }

    private fun onLogin() {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            delay(2_000)
            _uiState.update { state -> state.copy(isLoading = false, errorMsg = "email or password are incorrect") }
        }
    }
}