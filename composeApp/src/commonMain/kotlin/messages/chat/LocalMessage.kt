package messages.chat

data class LocalMessage(val content: String, val timeStamp: String, val sender: MessageSender)