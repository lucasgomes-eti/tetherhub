package messages.rooms.data

import response.ChatResponse

data class LocalRoom(
    val chat: ChatResponse,
    val lastMessage: String?,
)
