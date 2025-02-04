package eti.lucasgomes.tetherhub.chat

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import eti.lucasgomes.tetherhub.dsl.findById
import eti.lucasgomes.tetherhub.dsl.insertOne
import org.bson.types.ObjectId

class ChatRepository(private val mongoDatabase: MongoDatabase) {
    suspend fun insertOne(chat: ChatEntity) = mongoDatabase.insertOne(chat)

    suspend fun findById(id: ObjectId) = mongoDatabase.findById<ChatEntity>(id)
}