package messages.rooms

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import messages.ChatClient
import messages.chat.ChatScreen
import dsl.navigation.NavigationAction
import network.onError
import network.onSuccess

class RoomsScreenModel(private val chatClient: ChatClient) : ScreenModel {

    private val _uiState = MutableStateFlow(
        RoomsUiState(
            rooms = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    init {
        fetchRooms()
    }

    fun onAction(action: RoomsAction) {
        when (action) {
            is RoomsAction.OpenNewChat -> onOpenNewChat(action.chatId)
        }
    }

    private fun onOpenNewChat(chatId: String) {
        screenModelScope.launch {
            _navigationActions.send(NavigationAction.Push(ChatScreen(chatId)))
        }
    }

    private fun fetchRooms() {
        screenModelScope.launch {
            chatClient.getRooms().onError { }
                .onSuccess { _uiState.update { state -> state.copy(rooms = it) } }
        }
    }
}