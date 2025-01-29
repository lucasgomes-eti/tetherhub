package profile

import EventBus
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import navigation.NavigationAction
import network.Resource
import network.onError
import network.onSuccess
import post.PostClient
import post.detail.EditPostScreen
import post.detail.PostUpdated

class ProfileScreenModel(
    private val profileClient: ProfileClient,
    private val postClient: PostClient,
    private val eventBus: EventBus
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            username = "-",
            email = "-",
            isLoading = false,
            errorMsg = "",
            myPosts = emptyList(),
            event = ProfileEvent.None
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        fetchProfile()
        fetchMyPosts()
        subscribeToPostUpdates()
    }

    private fun subscribeToPostUpdates() {
        screenModelScope.launch {
            eventBus.subscribe<PostUpdated> {
                fetchMyPosts()
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.LikeMyPost -> onPostLiked(action.postId)
            is ProfileAction.DismissError -> onDismissError()
            is ProfileAction.DeleteMyPost -> onDeleteMyPost(action.postId)
            is ProfileAction.EditMyPost -> onEditMyPost(action.postId)
        }
    }

    private fun onEditMyPost(postId: String) {
        screenModelScope.launch {
            _navigationActions.send(
                NavigationAction.Push(
                    EditPostScreen(
                        postId
                    )
                )
            )
        }
    }

    private fun onDeleteMyPost(postId: String) {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            postClient.deleteMyPost(postId)
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false) }
                    eventBus.publish(PostUpdated)
                }
                .onError {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMsg = it.formatedMessage
                        )
                    }
                }
        }
    }

    private fun onDismissError() {
        _uiState.update { state -> state.copy(errorMsg = "") }
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            when (val response = profileClient.getProfile()) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            username = response.data.username,
                            email = response.data.email,
                            isLoading = false
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            username = "-",
                            email = "-",
                            isLoading = false,
                            errorMsg = "${response.error.internalCode} - ${response.error.message}"
                        )
                    }
                }
            }
        }
    }

    private fun fetchMyPosts() {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            when (val response = postClient.getMyPosts()) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            myPosts = response.data,
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

    private fun onPostLiked(postId: String) {
        screenModelScope.launch {
            when (val result = postClient.toggleLike(postId)) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        val postIndex = state.myPosts.indexOfFirst { it.id == postId }
                        state.copy(myPosts = state.myPosts.toMutableList().apply {
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
}