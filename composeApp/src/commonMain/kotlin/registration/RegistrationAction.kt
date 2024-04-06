package registration

sealed class RegistrationAction {
    data class EmailChanged(val value: String) : RegistrationAction()
    data class UsernameChanged(val value: String) : RegistrationAction()
    data class PasswordChanged(val value: String) : RegistrationAction()
    data class PasswordConfirmationChanged(val value: String) : RegistrationAction()

    data object CreateAccount : RegistrationAction()
}