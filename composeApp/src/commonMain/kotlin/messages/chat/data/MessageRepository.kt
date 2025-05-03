package messages.chat.data

import response.MessageResponse

class MessageRepository(private val messageDao: MessageDao) {

    suspend fun saveMessage(message: MessageResponse, chatId: String) {
        messageDao.insertMessage(
            MessageEntity(
                chatId = chatId,
                senderId = message.senderId,
                senderUsername = message.senderUsername,
                content = message.content,
                at = message.at.toEpochMilliseconds()
            )
        )
    }

    suspend fun getMessages(chatId: String): List<MessageEntity> {
        return messageDao.getMessages(chatId)
    }

    suspend fun getLastMessage(chatId: String): MessageEntity? {
        return messageDao.getLatestMessage(chatId)
    }
}