package post.feed

import DeepLink
import DeepLinkDestination
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
import network.onError
import network.onSuccess
import post.PostClient
import post.detail.CreatePostScreen
import post.detail.PostUpdated
import profile.search.SearchProfileScreen
import response.PageResponse

class FeedScreenModel(
    private var deepLink: DeepLink? = null,
    private val postClient: PostClient,
    private val eventBus: EventBus
) : ScreenModel {

    private val _uiState =
        MutableStateFlow(
            FeedUiState(
                posts = PageResponse(
                    items = emptyList(),
                    totalPages = 0,
                    totalItems = 0,
                    currentPage = 1,
                    lastPage = true
                ),
                isLoading = false,
                errorMsg = "",
                searchQuery = ""
            )
        )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        fetchPosts(1)
        subscribeToPostUpdates()
    }

    private fun handleDeepLink() {
        deepLink?.let { deepLink ->
            if (deepLink.destination != DeepLinkDestination.FRIENDS) return
            onNavigateToFriends()
            this.deepLink = null
        } ?: return
    }

    private fun subscribeToPostUpdates() {
        screenModelScope.launch {
            eventBus.subscribe<PostUpdated> {
                fetchPosts(1)
            }
        }
    }

    private fun onFetchMore() {
        if (_uiState.value.posts.lastPage.not()) {
            val page = _uiState.value.posts.currentPage + 1
            fetchPosts(page)
        }
    }

    private fun fetchPosts(page: Int) = withScreenModelScope {
        _uiState.update { state -> state.copy(isLoading = true) }
        postClient.getPosts(page).onError {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMsg = it.formatedMessage
                )
            }
        }.onSuccess {
            val posts = if (page > 1) _uiState.value.posts.items.toMutableList() else emptyList()
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMsg = "",
                    posts = it.copy(items = posts + it.items)
                )
            }
        }
    }

    fun onAction(feedAction: FeedAction) {
        when (feedAction) {
            is FeedAction.Like -> onPostLiked(feedAction.postId)
            is FeedAction.DismissError -> onDismissError()
            is FeedAction.Refresh -> fetchPosts(1)
            FeedAction.CancelSearch -> onCancelSearch()
            FeedAction.Search -> onSearch()
            is FeedAction.SearchQueryChanged -> onSearchQueryChanged(feedAction.query)
            FeedAction.CreatePost -> onCreatePost()
            FeedAction.NavigateToFriends -> onNavigateToFriends()
            FeedAction.Created -> onCreated()
            FeedAction.FetchMore -> onFetchMore()
        }
    }

    private fun onCreated() {
        handleDeepLink()
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

    private fun onPostLiked(postId: String) = withScreenModelScope {
        postClient.toggleLike(postId).onError {
            _uiState.update { state -> state.copy(errorMsg = it.formatedMessage) }
        }.onSuccess {
            _uiState.update { state ->
                val postIndex = state.posts.items.indexOfFirst { it.id == postId }
                val posts = state.posts.items.toMutableList().apply {
                    set(postIndex, it)
                }
                state.copy(
                    posts = state.posts.copy(items = posts)
                )
            }
        }
    }

    private fun onDismissError() {
        _uiState.update { state -> state.copy(errorMsg = "") }
    }

}