package profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import feed.FeedClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import util.Result

class ProfileScreenModel(
    private val profileClient: ProfileClient,
    private val feedClient: FeedClient
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            username = "-",
            email = "-",
            isLoading = false,
            errorMsg = "",
            myPosts = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfile()
        fetchMyPosts()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.LikeMyPost -> onPostLiked(action.postId)
            ProfileAction.DismissError -> onDismissError()
        }
    }

    private fun onDismissError() {
        _uiState.update { state -> state.copy(errorMsg = "") }
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            when (val response = profileClient.getProfile()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            username = response.data.username,
                            email = response.data.email,
                            isLoading = false
                        )
                    }
                }

                is Result.Error -> {
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
            when (val response = feedClient.getMyPosts()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            myPosts = response.data,
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

    private fun onPostLiked(postId: String) {
        screenModelScope.launch {
            when (val result = feedClient.toggleLike(postId)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        val postIndex = state.myPosts.indexOfFirst { it.id == postId }
                        state.copy(myPosts = state.myPosts.toMutableList().apply {
                            set(postIndex, result.data)
                        }.toList())
                    }
                }

                is Result.Error -> {
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