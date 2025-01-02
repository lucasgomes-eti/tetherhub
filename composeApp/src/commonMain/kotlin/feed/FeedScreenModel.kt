package feed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import util.Result

class FeedScreenModel(private val feedClient: FeedClient) : ScreenModel {

    private val _uiState =
        MutableStateFlow(FeedUiState(posts = emptyList(), isLoading = false, errorMsg = ""))
    val uiState = _uiState.asStateFlow()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            when (val response = feedClient.getPosts()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            posts = response.data,
                            isLoading = false,
                            errorMsg = ""
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMsg = "${response.error.internalCode} - ${response.error.message}"
                        )
                    }
                }
            }
        }
    }

    fun onAction(feedAction: FeedAction) {
        when (feedAction) {
            is FeedAction.Like -> onPostLiked(feedAction.postId)
            is FeedAction.DismissError -> onDismissError()
            is FeedAction.Refresh -> fetchPosts()
        }
    }

    private fun onPostLiked(postId: String) {
//        screenModelScope.launch {
//            _uiState.update { state ->
//                state.copy(posts = _uiState.value.posts.map {
//                    val isLiked = if (it.id == postId) !it.isLiked else it.isLiked
//                    it.copy(
//                        post = Post(
//                            id = it.id,
//                            author = it.author,
//                            content = it.content,
//                            likes = if (it.id == postId) if (isLiked) it.likes + 1 else it.likes - 1 else it.likes
//                        ),
//                        isLiked = isLiked
//                    )
//                })
//            }
//        }
    }

    private fun onDismissError() {
        _uiState.update { state -> state.copy(errorMsg = "") }
    }

}