package messages.rooms

import cafe.adriel.voyager.core.model.ScreenModel
import dsl.eventbus.EventBus
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import messages.ChatClient
import messages.chat.ChatScreen
import messages.chat.data.MessageRepository
import messages.newroom.NewRoomScreen
import messages.newroom.RoomCreated
import messages.rooms.data.LocalRoom
import network.onError
import network.onSuccess

class RoomsScreenModel(
    private val chatClient: ChatClient,
    private val eventBus: EventBus,
    private val messageRepository: MessageRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        RoomsUiState(
            rooms = emptyList(),
            isLoading = false,
            errorMessage = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        subscribeToRoomUpdates()
    }

    private fun subscribeToRoomUpdates() = withScreenModelScope {
        eventBus.subscribe<RoomCreated> { fetchRooms() }
    }

    fun onAction(action: RoomsAction) {
        when (action) {
            is RoomsAction.OpenNewChat -> onOpenNewChat(action.chatId)
            RoomsAction.CreateNewRoom -> onCreateNewRoom()
            RoomsAction.Refresh -> fetchRooms()
            RoomsAction.DismissError -> onDismissError()
        }
    }

    private fun onDismissError() = withScreenModelScope {
        _uiState.update { state -> state.copy(errorMessage = "") }
    }

    private fun onCreateNewRoom() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Push(NewRoomScreen))
    }

    private fun onOpenNewChat(chatId: String) = withScreenModelScope {
        _navigationActions.send(NavigationAction.Push(ChatScreen(chatId)))
    }

    private fun fetchRooms() = withScreenModelScope {
        _uiState.update { state -> state.copy(isLoading = true) }
        chatClient.getRooms().onError {
            _uiState.update { state ->
                state.copy(errorMessage = it.formatedMessage, isLoading = false)
            }
        }.onSuccess {

            _uiState.update { state ->
                state.copy(
                    rooms = it.map {
                        LocalRoom(
                            chat = it,
                            lastMessage = messageRepository.getLastMessage(it.chatId)?.content
                        )
                    },
                    isLoading = false,
                    errorMessage = ""
                )
            }
        }
    }
}