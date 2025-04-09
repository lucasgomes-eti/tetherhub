package messages.rooms

import response.ChatResponse

data class RoomsUiState(
    val rooms: List<ChatResponse>,
    val isLoading: Boolean,
    val errorMessage: String
)