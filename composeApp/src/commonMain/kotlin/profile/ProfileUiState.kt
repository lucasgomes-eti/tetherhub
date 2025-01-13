package profile

import response.PostResponse

data class ProfileUiState(
    val username: String,
    val email: String,
    val isLoading: Boolean,
    val errorMsg: String,
    val myPosts: List<PostResponse>
)