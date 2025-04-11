package messages.chat.data

import io.realm.kotlin.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import response.MessageResponse

class MessageRepository(private val realm: Realm) {

    suspend fun saveMessage(message: MessageResponse, chatId: String) {
        realm.write {
            copyToRealm(MessageEntity().apply {
                senderId = message.senderId
                senderUsername = message.senderUsername
                content = message.content
                this.chatId = chatId
                at = message.at.toEpochMilliseconds()
            })
        }
    }

    suspend fun getMessages(chatId: String): List<MessageEntity> {
        return realm.query(MessageEntity::class, "chatId == $0", chatId).find()
            .sortedByDescending { it.at }.asFlow().toList()
    }


    suspend fun getLastMessage(chatId: String): MessageEntity? {
        return withContext(Dispatchers.IO) {
            realm.query(MessageEntity::class, "chatId == $0", chatId).find()
                .maxByOrNull { it.at }
        }
    }
}