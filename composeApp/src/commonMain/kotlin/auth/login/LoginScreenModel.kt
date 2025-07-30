package auth.login

import RegexConstants
import auth.registration.RegistrationScreen
import cafe.adriel.voyager.core.model.ScreenModel
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import home.HomeScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import network.onError
import network.onSuccess

class LoginScreenModel(private val loginClient: LoginClient) : ScreenModel {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            email = "",
            password = "",
            errorMsg = "",
            isLoading = false,
            emailError = "",
            passwordError = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    fun onAction(loginAction: LoginAction) {
        when (loginAction) {
            is LoginAction.EmailChanged -> onEmailChanged(loginAction.value)
            is LoginAction.PasswordChanged -> onPasswordChanged(loginAction.value)
            is LoginAction.Login -> onLogin()
            LoginAction.Registration -> onRegistration()
        }
    }

    private fun onRegistration() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Push(RegistrationScreen))
    }

    private fun onEmailChanged(value: String) {
        _uiState.update { state -> state.copy(email = value, errorMsg = "") }
    }

    private fun onPasswordChanged(value: String) {
        _uiState.update { state -> state.copy(password = value, errorMsg = "") }
    }

    private fun onLogin() = withScreenModelScope {
        if (isUiStateValid(uiState.value)) {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    errorMsg = "",
                    emailError = "",
                    passwordError = ""
                )
            }
            loginClient.authenticateWithCredentials(
                uiState.value.email,
                uiState.value.password
            ).onError {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMsg = it.formatedMessage
                    )
                }
            }.onSuccess {
                _uiState.update { state -> state.copy(isLoading = false) }
                _navigationActions.send(NavigationAction.Replace(HomeScreen()))
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