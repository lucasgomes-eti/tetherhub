package eti.lucasgomes.tetherhub.post

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class PostEntity(
    @Contextual
    val id: ObjectId,
    val author: String,
    val content: String,
    @Serializable
    val likes: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant?
)
