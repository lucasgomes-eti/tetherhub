package messages.chat.data

import numberOfPagesFor
import response.MessageResponse
import response.PageResponse
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
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

    suspend fun getMessages(chatId: String, page: Int, size: Int): PageResponse<MessageEntity> {
        val totalItems = messageDao.countMessages()
        val totalPages = totalItems numberOfPagesFor size
        val skip = (page - 1) * size
        val items = messageDao.getMessages(chatId = chatId, limit = size, skip = skip)

        return PageResponse(
            items = items,
            totalPages = totalPages,
            totalItems = totalItems,
            currentPage = page,
            lastPage = page >= totalPages
        )
    }
}