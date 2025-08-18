package post.detail

import DATE_TIME_PRESENTATION_FORMAT
import PUBLICATION_WORD_LIMIT
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dsl.eventbus.EventBus
import dsl.navigation.NavigationAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import network.onError
import network.onSuccess
import post.PostClient
import request.CreatePostRequest
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CreateOrEditPostScreenModel(
    private val postClient: PostClient,
    private val eventBus: EventBus,
    private val postId: String?
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        CreateOrEditPostUiState(
            publication = "",
            isLoading = false,
            publicationFieldError = "",
            errorMsg = "",
            topAppBarTitle = "New Post"
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        if (postId != null) {
            fetchPost(postId)
        }
    }

    private fun fetchPost(postId: String) {
        screenModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true)
            }
            postClient.getPostById(postId).onError {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMsg = it.formatedMessage
                    )
                }
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMsg = "",
                        publication = it.content,
                        topAppBarTitle = it.createdAt
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .format(DATE_TIME_PRESENTATION_FORMAT)
                    )
                }
            }
        }
    }

    fun onAction(action: CreateOrEditPostAction) {
        when (action) {
            is CreateOrEditPostAction.PublicationChanged -> onPublicationChanged(action.value)
            is CreateOrEditPostAction.PublishOrEditPost -> onPublishPost()
            is CreateOrEditPostAction.DismissError -> onDismissError()
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
                postId?.let { updatePostContent(it, uiStateSnapshot.publication) }
                    ?: publishNewPost(uiStateSnapshot)
            }
        }
    }

    private suspend fun updatePostContent(id: String, newContent: String) {
        postClient.updateContent(id, newContent).onError {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    publicationFieldError = "",
                    errorMsg = "${it.internalCode} - ${it.message}"
                )
            }
        }.onSuccess {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    publicationFieldError = "",
                    errorMsg = ""
                )
            }
            _navigationActions.send(NavigationAction.Pop)
            eventBus.publish(PostUpdated)
        }
    }

    private suspend fun publishNewPost(uiStateSnapshot: CreateOrEditPostUiState) {
        postClient.publishPost(CreatePostRequest(uiStateSnapshot.publication)).onError {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    publicationFieldError = "",
                    errorMsg = "${it.internalCode} - ${it.message}"
                )
            }
        }.onSuccess {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    publicationFieldError = "",
                    errorMsg = ""
                )
            }
            _navigationActions.send(NavigationAction.Pop)
            eventBus.publish(PostUpdated)
        }
    }

    private fun isPublicationContentValid(publication: String): Boolean {
        if (publication.isEmpty()) {
            _uiState.update { state ->
                state.copy(
                    publicationFieldError = "Can't publish an empty post!",
                    isLoading = false
                )
            }
            return false
        }
        return true
    }

    private fun onPublicationChanged(value: String) {
        if (value.length <= PUBLICATION_WORD_LIMIT) {
            _uiState.update { state ->
                state.copy(
                    publication = value,
                    publicationFieldError = ""
                )
            }
        }
    }
}