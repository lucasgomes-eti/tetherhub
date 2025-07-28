package auth.login

import RegexConstants
import cafe.adriel.voyager.core.model.ScreenModel
import dsl.withScreenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import network.Resource

class LoginScreenModel(private val loginClient: LoginClient) : ScreenModel {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            email = "",
            password = "",
            errorMsg = "",
            isLoading = false,
            event = LoginEvent.NONE,
            emailError = "",
            passwordError = ""
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

    private fun onLogin() = withScreenModelScope {
        val uiStateSnapshot = _uiState.value
        if (isUiStateValid(uiStateSnapshot)) {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    errorMsg = "",
                    emailError = "",
                    passwordError = ""
                )
            }
            val result = loginClient.authenticateWithCredentials(
                uiStateSnapshot.email,
                uiStateSnapshot.password
            )
            when (result) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            event = LoginEvent.SUCCESS
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            event = LoginEvent.NONE,
                            errorMsg = "${result.error.internalCode} - ${result.error.message}"
                        )
                    }
                }
            }
        }
    }

    private fun isUiStateValid(uiStateSnapshot: LoginUiState) =
        isEmailValid(uiStateSnapshot.email) and isPasswordValid(uiStateSnapshot.password)

    private fun isEmailValid(email: String): Boolean {
        if (email.isEmpty()) {
            _uiState.update { state -> state.copy(emailError = "email must not be empty") }
            return false
        }
        if (!email.matches(RegexConstants.EMAIL)) {
            _uiState.update { state -> state.copy(emailError = "email must have valid format") }
            return false
        }
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.isEmpty()) {
            _uiState.update { state -> state.copy(passwordError = "password must not be empty") }
            return false
        }
        return true
    }
}