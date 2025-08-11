package post.feed

import response.PageResponse
import response.PostResponse

data class FeedUiState(
    val posts: PageResponse<PostResponse>,
    val isLoading: Boolean,
    val errorMsg: String,
    val searchQuery: String,
)