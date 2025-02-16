package messages.chat

import Message
import response.UserResponse

data class ChatUiState(
    val users: List<UserResponse>,
    val messages: List<Message>,
    val draft: String
)