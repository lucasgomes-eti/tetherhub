package auth.login

data class LoginUiState(
    val email: String,
    val password: String,
    val errorMsg: String,
    val isLoading: Boolean,
    val emailError: String,
    val passwordError: String
)