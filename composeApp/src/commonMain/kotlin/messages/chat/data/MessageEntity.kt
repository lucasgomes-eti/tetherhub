package messages.chat.data

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class MessageEntity : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var chatId: String = ""
    var senderId: String = ""
    var senderUsername: String = ""
    var content: String = ""
    var at: Long = 0
}