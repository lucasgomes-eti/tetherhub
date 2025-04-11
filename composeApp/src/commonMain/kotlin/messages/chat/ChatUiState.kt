package messages.chat

import messages.chat.data.LocalMessage
import response.UserResponse

data class ChatUiState(
    val users: List<UserResponse>,
    val messages: List<LocalMessage>,
    val draft: String
)