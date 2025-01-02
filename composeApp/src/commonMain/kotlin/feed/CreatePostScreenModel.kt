package feed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import request.CreatePostRequest
import util.Result

class CreatePostScreenModel(private val feedClient: FeedClient) : ScreenModel {

    private val _uiState = MutableStateFlow(
        CreatePostUiState(
            publication = "",
            isLoading = false,
            publicationFieldError = "",
            errorMsg = "",
            event = CreatePostEvent.NONE
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(action: CreatePostAction) {
        when (action) {
            is CreatePostAction.PublicationChanged -> onPublicationChanged(action.value)
            is CreatePostAction.PublishPost -> onPublishPost()
            is CreatePostAction.DismissError -> onDismissError()
        }
    }

    private fun onDismissError() {
        _uiState.update { state -> state.copy(errorMsg = "") }
    }

    private fun onPublishPost() {
        screenModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    publicationFieldError = "",
                    errorMsg = ""
                )
            }
            val uiStateSnapshot = _uiState.value
            if (isPublicationContentValid(uiStateSnapshot.publication)) {
                when (val response =
                    feedClient.publishPost(CreatePostRequest(uiStateSnapshot.publication))) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                publicationFieldError = "",
                                errorMsg = "",
                                event = CreatePostEvent.SUCCESS
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                publicationFieldError = "",
                                errorMsg = "${response.error.internalCode} - ${response.error.message}",
                                event = CreatePostEvent.NONE
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isPublicationContentValid(publication: String): Boolean {
        if (publication.isEmpty()) {
            _uiState.update { state -> state.copy(publicationFieldError = "Can't publish an empty post!") }
            return false
        }
        return true
    }

    private fun onPublicationChanged(value: String) {
        _uiState.update { state -> state.copy(publication = value, publicationFieldError = "") }
    }
}