package auth.login

sealed class LoginAction {
    data class EmailChanged(val value: String) : LoginAction()

    data class PasswordChanged(val value: String) : LoginAction()

    data object Login : LoginAction()
}