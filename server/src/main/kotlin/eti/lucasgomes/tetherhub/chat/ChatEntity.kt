package eti.lucasgomes.tetherhub.chat

import eti.lucasgomes.tetherhub.dsl.MongoEntity
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
@MongoEntity("chat")
data class ChatEntity(
    @Contextual
    val id: ObjectId,
    val roomName: String,
    val users: List<String>
)
