package messages.chat.data

import messages.chat.MessageSender

data class LocalMessage(
    val content: String,
    val timeStamp: String,
    val sender: MessageSender,
    val senderUsername: String,
)