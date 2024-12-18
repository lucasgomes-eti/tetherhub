package profile

data class ProfileUiState(
    val username: String,
    val email: String,
    val isLoading: Boolean,
    val errorMsg: String
)