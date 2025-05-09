package friends

import cafe.adriel.voyager.core.model.ScreenModel
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import network.onError
import network.onSuccess
import profile.search.SearchProfileScreen
import response.PublicProfileResponse

class FriendsScreenModel(private val friendsClient: FriendsClient) : ScreenModel {

    private val _uiState = MutableStateFlow(
        FriendsUiState(
            isLoading = false,
            requests = emptyList(),
            friends = emptyList(),
            errorMessage = "",
            searchQuery = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        fetchRequests()
        fetchFriends()
    }

    fun onAction(action: FriendsAction) {
        when (action) {
            FriendsAction.CancelSearch -> onCancelSearch()
            FriendsAction.NavigateBack -> onNavigateBack()
            is FriendsAction.SearchQueryChanged -> onSearchQueryChanged(action.query)
            FriendsAction.Search -> onSearch()
            is FriendsAction.AcceptRequest -> onAcceptRequest(action.id)
            FriendsAction.DismissError -> onDismissError()
        }
    }

    private fun onDismissError() = withScreenModelScope {
        _uiState.update { state -> state.copy(errorMessage = "") }
    }

    private fun onAcceptRequest(id: String) = withScreenModelScope {
        _uiState.update { state -> state.copy(isLoading = true) }
        friendsClient.acceptFriendshipRequest(id).onError {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage = it.formatedMessage
                )
            }
        }.onSuccess {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    requests = state.requests.filter { it.id != id },
                    friends = state.friends.toMutableSet().apply {
                        add(state.requests.find { it.id == id }!!.run {
                            PublicProfileResponse(
                                id,
                                fromUsername,
                                PublicProfileResponse.RelationshipStatus.FRIENDS
                            )
                        })
                    }.toList()
                )
            }
        }
    }

    private fun onSearchQueryChanged(query: String) = withScreenModelScope {
        _uiState.update { state -> state.copy(searchQuery = query) }
    }

    private fun onSearch() = withScreenModelScope {
        val searchQuery = _uiState.value.searchQuery
        if (searchQuery.isNotBlank()) {
            _navigationActions.send(NavigationAction.Push(SearchProfileScreen(searchQuery)))
        }
    }

    private fun onNavigateBack() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Pop)
    }

    private fun onCancelSearch() = withScreenModelScope {
        _uiState.update { state -> state.copy(searchQuery = "") }
    }

    private fun fetchRequests() = withScreenModelScope {
        _uiState.update { state -> state.copy(isLoading = true) }
        friendsClient.getFriendRequests().onError {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage = it.formatedMessage
                )
            }
        }.onSuccess { _uiState.update { state -> state.copy(isLoading = false, requests = it) } }
    }

    private fun fetchFriends() = withScreenModelScope {
        _uiState.update { state -> state.copy(isLoading = true) }
        friendsClient.getFriends().onError {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage = it.formatedMessage
                )
            }
        }.onSuccess { _uiState.update { state -> state.copy(isLoading = false, friends = it) } }
    }
}