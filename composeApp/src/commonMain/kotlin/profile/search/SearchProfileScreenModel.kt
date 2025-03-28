package profile.search

import cafe.adriel.voyager.core.model.ScreenModel
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import friends.FriendsClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import network.onError
import network.onSuccess
import profile.ProfileClient
import request.FriendshipSolicitationRequest
import response.PageResponse
import response.PublicProfileResponse

class SearchProfileScreenModel(
    private val searchQuery: String,
    private val profileClient: ProfileClient,
    private val friendsClient: FriendsClient
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        SearchProfileUiState(
            searchQuery = searchQuery,
            isLoading = false,
            errorMessage = "",
            profiles = PageResponse(
                items = emptyList(),
                totalPages = 0,
                totalItems = 0,
                currentPage = 0,
                lastPage = true
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        fetchProfiles(1)
    }

    fun onAction(action: SearchProfileAction) {
        when (action) {
            SearchProfileAction.NavigateBack -> onNavigateBack()
            SearchProfileAction.FetchMore -> onFetchMore()
            SearchProfileAction.DismissError -> onDismissError()
            is SearchProfileAction.InviteFriend -> onInviteFriend(action.profile)
        }
    }

    private fun onInviteFriend(profile: PublicProfileResponse) = withScreenModelScope {
        friendsClient.postFriendshipRequest(FriendshipSolicitationRequest(profile.id)).onError {
            _uiState.update { state ->
                state.copy(errorMessage = it.formatedMessage)
            }
        }.onSuccess {
            val profiles = _uiState.value.profiles.items.toMutableList().apply {
                val index = indexOf(profile)
                removeAt(index)
                add(
                    index,
                    profile.copy(relationshipStatus = PublicProfileResponse.RelationshipStatus.PENDING)
                )
            }.toList()
            _uiState.update { state ->
                state.copy(profiles = state.profiles.copy(items = profiles))
            }
        }
    }

    private fun onDismissError() = withScreenModelScope {
        _uiState.update { state -> state.copy(errorMessage = "") }
    }

    private fun onFetchMore() {
        if (_uiState.value.profiles.lastPage.not()) {
            val page = _uiState.value.profiles.currentPage + 1
            fetchProfiles(page)
        }
    }

    private fun fetchProfiles(page: Int) = withScreenModelScope {
        _uiState.update { state -> state.copy(isLoading = true) }
        profileClient.getProfilesByUsername(searchQuery, page).onError {
            _uiState.update { state -> state.copy(errorMessage = it.message, isLoading = false) }
        }.onSuccess {
            val profiles = _uiState.value.profiles.items.toMutableList()
            _uiState.update { state ->
                state.copy(
                    profiles = it.copy(items = profiles + it.items),
                    isLoading = false
                )
            }
        }
    }

    private fun onNavigateBack() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Pop)
    }
}
