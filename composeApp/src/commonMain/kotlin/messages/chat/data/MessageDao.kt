package messages.chat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messageEntity WHERE chatId = :chatId ORDER BY at DESC")
    suspend fun getMessages(chatId: String): List<MessageEntity>

    @Query("SELECT * FROM messageEntity WHERE chatId = :chatId ORDER BY at DESC LIMIT 1")
    suspend fun getLatestMessage(chatId: String): MessageEntity?

    @Query("SELECT * FROM messageEntity WHERE chatId = :chatId ORDER BY at ASC LIMIT :limit OFFSET :skip")
    suspend fun getMessages(chatId: String, limit: Int, skip: Int): List<MessageEntity>

    @Query("SELECT COUNT(*) FROM messageEntity")
    suspend fun countMessages(): Long
}