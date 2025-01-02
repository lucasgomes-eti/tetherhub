package feed

import response.PostResponse

data class FeedUiState(
    val posts: List<PostResponse>,
    val isLoading: Boolean,
    val errorMsg: String
)