package post.feed

import dsl.eventbus.EventBus
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.Resource
import post.PostClient
import post.detail.PostUpdated

class FeedScreenModel(
    private val postClient: PostClient,
    private val eventBus: EventBus
) : ScreenModel {

    private val _uiState =
        MutableStateFlow(FeedUiState(posts = emptyList(), isLoading = false, errorMsg = ""))
    val uiState = _uiState.asStateFlow()

    init {
        fetchPosts()
        subscribeToPostUpdates()
    }

    private fun subscribeToPostUpdates() {
        screenModelScope.launch {
            eventBus.subscribe<PostUpdated> {
                fetchPosts()
            }
        }
    }

    private fun fetchPosts() {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            when (val response = postClient.getPosts()) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            posts = response.data,
                            isLoading = false,
                            errorMsg = ""
                        )
                    }
                }

                is Resource.Error -> {
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
        screenModelScope.launch {
            when (val result = postClient.toggleLike(postId)) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        val postIndex = state.posts.indexOfFirst { it.id == postId }
                        state.copy(posts = state.posts.toMutableList().apply {
                            set(postIndex, result.data)
                        }.toList())
                    }
                }

                is Resource.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            errorMsg = "${result.error.internalCode} - ${result.error.message}"
                        )
                    }
                }
            }
        }
    }

    private fun onDismissError() {
        _uiState.update { state -> state.copy(errorMsg = "") }
    }

}