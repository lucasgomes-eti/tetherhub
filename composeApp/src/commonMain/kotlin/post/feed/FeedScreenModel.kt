package post.feed

import DeepLink
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dsl.eventbus.EventBus
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import friends.FriendsScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.Resource
import post.PostClient
import post.detail.CreatePostScreen
import post.detail.PostUpdated
import profile.search.SearchProfileScreen

class FeedScreenModel(
    private val deepLink: DeepLink? = null,
    private val postClient: PostClient,
    private val eventBus: EventBus
) : ScreenModel {

    private val _uiState =
        MutableStateFlow(
            FeedUiState(
                posts = emptyList(),
                isLoading = false,
                errorMsg = "",
                searchQuery = ""
            )
        )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        fetchPosts()
        subscribeToPostUpdates()
        handleDeepLink()
    }

    private fun handleDeepLink() {
        deepLink ?: return
        onNavigateToFriends()
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
            FeedAction.CancelSearch -> onCancelSearch()
            FeedAction.Search -> onSearch()
            is FeedAction.SearchQueryChanged -> onSearchQueryChanged(feedAction.query)
            FeedAction.CreatePost -> onCreatePost()
            FeedAction.NavigateToFriends -> onNavigateToFriends()
        }
    }

    private fun onNavigateToFriends() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Push(FriendsScreen))
    }

    private fun onCreatePost() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Push(CreatePostScreen))
    }

    private fun onSearch() = withScreenModelScope {
        val searchQuery = _uiState.value.searchQuery
        if (searchQuery.isNotBlank()) {
            _navigationActions.send(NavigationAction.Push(SearchProfileScreen(searchQuery)))
        }
    }

    private fun onSearchQueryChanged(query: String) = withScreenModelScope {
        _uiState.update { state -> state.copy(searchQuery = query) }
    }

    private fun onCancelSearch() = withScreenModelScope {
        _uiState.update { state -> state.copy(searchQuery = "") }
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