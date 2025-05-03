package messages.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val chatId: String,
    val senderId: String,
    val senderUsername: String,
    val content: String,
    val at: Long
)