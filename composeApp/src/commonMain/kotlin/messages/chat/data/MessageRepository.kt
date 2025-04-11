package messages.chat.data

import io.realm.kotlin.Realm
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
            .sortedByDescending { it.at }
    }

}