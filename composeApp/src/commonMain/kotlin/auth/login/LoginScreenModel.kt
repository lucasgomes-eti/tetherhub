package auth.login

import RegexConstants
import cafe.adriel.voyager.core.model.ScreenModel
import com.mmk.kmpnotifier.notification.Notifier
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import dsl.withScreenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import network.Resource

class LoginScreenModel(
    private val loginClient: LoginClient,
    private val permissionsController: PermissionsController,
    private val notifier: Notifier
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            email = "lucas@eti.com",
            password = "@Pass123",
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

    private fun displayLocalNotification() {
        notifier.notify { title = "Notification title"; body = "Notification text" }
    }

    private suspend fun askPermissionAndNotify() {
        try {
            permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
            displayLocalNotification()
        } catch (_: DeniedAlwaysException) {
        } catch (_: DeniedException) {
        }
    }

    private fun onLogin() = withScreenModelScope {
        if (permissionsController.isPermissionGranted(Permission.REMOTE_NOTIFICATION)) {
            displayLocalNotification()
        } else {
            askPermissionAndNotify()
        }

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