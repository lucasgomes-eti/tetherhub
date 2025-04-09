package messages.newroom

import DataStoreKeys
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import cafe.adriel.voyager.core.model.ScreenModel
import dsl.eventbus.EventBus
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import friends.FriendsClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import messages.ChatClient
import network.onError
import network.onSuccess
import profile.ProfileClient
import request.CreateChatRequest
import response.PublicProfileResponse

class NewRoomScreenModel(
    private val friendsClient: FriendsClient,
    private val chatClient: ChatClient,
    private val profileClient: ProfileClient,
    private val eventBus: EventBus,
    private val preferences: DataStore<Preferences>,
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        NewRoomUiState(
            name = "",
            users = emptyList(),
            selectedUsers = emptyList(),
            showAllSelected = false,
            errorMessage = "",
            isCreating = false,
            myUsername = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        fetchFriends()
        fetchMe()
    }

    fun onAction(action: NewRoomAction) {
        when (action) {
            NewRoomAction.NavigateBack -> onNavigateBack()
            is NewRoomAction.NameChanged -> onNameChanged(action.value)
            is NewRoomAction.AddUser -> onAddUser(action.user)
            is NewRoomAction.RemoveUser -> onRemoveUser(action.user)
            NewRoomAction.ShowAllSelected -> onShowAllSelected()
            NewRoomAction.DismissAllSelected -> onDismissAllSelected()
            NewRoomAction.DismissError -> onDismissError()
            NewRoomAction.CreateChat -> onCreateChat()
        }
    }

    private fun onCreateChat() = withScreenModelScope {
        _uiState.update { state -> state.copy(isCreating = true) }
        val myId =
            preferences.data.map { it[stringPreferencesKey(DataStoreKeys.USER_ID)] }.firstOrNull()
                ?: run {
                    _uiState.update { state ->
                        state.copy(
                            errorMessage = "Local user not found, please logout and try again",
                            isCreating = false
                        )
                    }
                    return@withScreenModelScope
                }
        chatClient.createRoom(CreateChatRequest(roomName = run {
            uiState.value.name.let { if (it.isNotBlank()) return@let it else null }
                ?: "${uiState.value.myUsername}, ${uiState.value.selectedUsers.joinToString { it.username }}"
        }, users = uiState.value.selectedUsers.map { it.id } + myId)).onError {
            _uiState.update { state ->
                state.copy(
                    errorMessage = it.formatedMessage,
                    isCreating = false
                )
            }
        }.onSuccess {
            _uiState.update { state -> state.copy(isCreating = false, errorMessage = "") }
            eventBus.publish(RoomCreated)
            _navigationActions.send(NavigationAction.Pop)
        }
    }

    private fun onDismissError() = withScreenModelScope {
        _uiState.update { state -> state.copy(errorMessage = "") }
    }

    private fun fetchFriends() = withScreenModelScope {
        friendsClient.getFriends().onError {
            _uiState.update { state -> state.copy(errorMessage = it.formatedMessage) }
        }.onSuccess {
            _uiState.update { state -> state.copy(users = it.map { NewRoomUser(it, false) }) }
        }
    }

    private fun fetchMe() = withScreenModelScope {
        profileClient.getProfile().onError {
            _uiState.update { state -> state.copy(errorMessage = it.formatedMessage) }
        }.onSuccess {
            _uiState.update { state -> state.copy(myUsername = it.username) }
        }
    }

    private fun onDismissAllSelected() = withScreenModelScope {
        _uiState.update { state -> state.copy(showAllSelected = false) }
    }

    private fun onShowAllSelected() = withScreenModelScope {
        _uiState.update { state -> state.copy(showAllSelected = true) }
    }

    private fun onRemoveUser(user: PublicProfileResponse) = withScreenModelScope {
        _uiState.update { state ->
            state.copy(
                selectedUsers = state.selectedUsers - user,
                users = state.users.toMutableList().apply {
                    val index = indexOfFirst { it.user == user }
                    removeAt(index)
                    add(index, NewRoomUser(user, false))
                }.toList()
            )
        }
    }

    private fun onAddUser(user: PublicProfileResponse) = withScreenModelScope {
        _uiState.update { state ->
            state.copy(
                selectedUsers = state.selectedUsers + user,
                users = state.users.toMutableList().apply {
                    val index = indexOfFirst { it.user == user }
                    removeAt(index)
                    add(index, NewRoomUser(user, true))
                }.toList()
            )
        }
    }

    private fun onNameChanged(value: String) = withScreenModelScope {
        _uiState.update { state -> state.copy(name = value) }
    }

    private fun onNavigateBack() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Pop)
    }
}