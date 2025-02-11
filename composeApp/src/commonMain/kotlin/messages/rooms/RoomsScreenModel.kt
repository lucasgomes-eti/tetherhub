package messages.rooms

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import messages.ChatClient
import network.onError
import network.onSuccess

class RoomsScreenModel(private val chatClient: ChatClient) : ScreenModel {

    private val _uiState = MutableStateFlow(
        RoomsUiState(
            rooms = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        fetchRooms()
    }

    private fun fetchRooms() {
        screenModelScope.launch {
            chatClient.getRooms().onError { }
                .onSuccess { _uiState.update { state -> state.copy(rooms = it) } }
        }
    }
}