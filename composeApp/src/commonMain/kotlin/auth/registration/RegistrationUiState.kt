package auth.registration

data class RegistrationUiState(
    val email: String,
    val emailError: String,
    val username: String,
    val usernameError: String,
    val password: String,
    val passwordError: String,
    val passwordConfirmation: String,
    val passwordConfirmationError: String,
    val isLoading: Boolean,
    val event: RegistrationEvent,
    val clientErrorMessage: String,
    val shouldShowClientError: Boolean
)