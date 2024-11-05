package auth.registration

import auth.registration.network.RegistrationClient
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import request.CreateUserRequest
import request.Email
import request.Password
import request.Username
import util.Result

class RegistrationScreenModel(private val registrationClient: RegistrationClient) : ScreenModel {

    private val _uiState = MutableStateFlow(
        RegistrationUiState(
            email = "",
            emailError = "",
            username = "",
            usernameError = "",
            password = "",
            passwordError = "",
            passwordConfirmation = "",
            passwordConfirmationError = "",
            isLoading = false,
            event = RegistrationEvent.NONE,
            requestResult = "request not sent"
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(registrationAction: RegistrationAction) {
        when (registrationAction) {
            is RegistrationAction.EmailChanged -> onEmailChanged(registrationAction.value)
            is RegistrationAction.UsernameChanged -> onUsernameChanged(registrationAction.value)
            is RegistrationAction.PasswordChanged -> onPasswordChanged(registrationAction.value)
            is RegistrationAction.PasswordConfirmationChanged -> onPasswordConfirmationChanged(
                registrationAction.value
            )

            is RegistrationAction.CreateAccount -> onCreateAccount()
        }
    }

    private fun onEmailChanged(value: String) {
        _uiState.update { state -> state.copy(email = value, emailError = "") }
    }

    private fun onUsernameChanged(value: String) {
        _uiState.update { state -> state.copy(username = value, usernameError = "") }
    }

    private fun onPasswordChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                password = value,
                passwordError = "",
                passwordConfirmationError = ""
            )
        }
    }

    private fun onPasswordConfirmationChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                passwordConfirmation = value,
                passwordConfirmationError = "",
                passwordError = ""
            )
        }
    }

    private fun onCreateAccount() {
        val uiStateSnapshot = _uiState.value
        if (isUiStateValid(uiStateSnapshot)) {
            screenModelScope.launch {
                _uiState.update { state -> state.copy(isLoading = true) }
                val response = registrationClient.submitNewUser(
                    CreateUserRequest(
                        Email(uiStateSnapshot.email),
                        Username(uiStateSnapshot.username),
                        Password(uiStateSnapshot.password)
                    )
                )
                when (response) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                event = RegistrationEvent.SUCCESS,
                                requestResult = "New user created with id: ${response.data.id}"
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                event = RegistrationEvent.NONE,
                                requestResult = "Error: ${response.error.name}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isUiStateValid(uiStateSnapshot: RegistrationUiState): Boolean {
        return isEmailValid(uiStateSnapshot.email) and isUsernameValid(uiStateSnapshot.username) and isPasswordValid(
            uiStateSnapshot.password,
            uiStateSnapshot.passwordConfirmation
        )
    }

    private fun isEmailValid(email: String): Boolean {
        if (email.isEmpty()) {
            _uiState.update { state -> state.copy(emailError = "email must not be empty") }
            return false
        }
        if (!email.matches(EMAIL_REGEX)) {
            _uiState.update { state -> state.copy(emailError = "email must have valid format") }
            return false
        }
        return true
    }

    private fun isUsernameValid(username: String): Boolean {
        if (username.isEmpty()) {
            _uiState.update { state -> state.copy(usernameError = "username must not be empty") }
            return false
        }
        if (!username.matches(USERNAME_REGEX)) {
            _uiState.update { state -> state.copy(usernameError = "username must not contain spaces or capital letters") }
            return false
        }
        return true
    }

    private fun isPasswordValid(password: String, passwordConfirmation: String): Boolean {
        if (password.isEmpty()) {
            _uiState.update { state -> state.copy(passwordError = "password must not be empty") }
            return false
        }
        if (password.length < 8) {
            _uiState.update { state -> state.copy(passwordError = "password must have at least 8 characters") }
            return false
        }
        if (password.contains(" ")) {
            _uiState.update { state -> state.copy(passwordError = "password must not contain spaces") }
            return false
        }
        if (!password.matches(PASSWORD_REGEX)) {
            _uiState.update { state -> state.copy(passwordError = "password must have letters and numbers") }
            return false
        }
        if (password != passwordConfirmation) {
            val error = "passwords do not match"
            _uiState.update { state ->
                state.copy(
                    passwordError = error,
                    passwordConfirmationError = error
                )
            }
            return false
        }
        return true
    }

    companion object {
        private val EMAIL_REGEX = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()
        private val USERNAME_REGEX = "^[a-z0-9_!@#\$%^&*()-+=]+\$".toRegex()
        private val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).+\$".toRegex()
    }
}