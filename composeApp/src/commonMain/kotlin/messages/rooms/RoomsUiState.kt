package messages.rooms

import response.ChatResponse

data class RoomsUiState(
    val rooms: List<ChatResponse>,
)