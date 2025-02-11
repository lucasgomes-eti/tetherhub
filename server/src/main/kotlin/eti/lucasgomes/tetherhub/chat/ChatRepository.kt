package eti.lucasgomes.tetherhub.chat

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import eti.lucasgomes.tetherhub.dsl.findById
import eti.lucasgomes.tetherhub.dsl.insertOne
import eti.lucasgomes.tetherhub.dsl.withCollection
import kotlinx.coroutines.flow.toList
import org.bson.BsonObjectId
import org.bson.Document
import org.bson.types.ObjectId

class ChatRepository(private val mongoDatabase: MongoDatabase) {
    suspend fun insertOne(chat: ChatEntity) = mongoDatabase.insertOne(chat)

    suspend fun findById(id: ObjectId) = mongoDatabase.findById<ChatEntity>(id)

    suspend fun findById(id: BsonObjectId) = mongoDatabase.findById<ChatEntity>(id)

    suspend fun findByUserId(userId: ObjectId) =
        mongoDatabase.withCollection<ChatEntity, List<ChatEntity>> {
            withDocumentClass<ChatEntity>()
                .find(Document("users", userId.toString())).toList()
        }
}