package messages.rooms

import response.CreateChatResponse

data class RoomsUiState(
    val rooms: List<CreateChatResponse>,
)