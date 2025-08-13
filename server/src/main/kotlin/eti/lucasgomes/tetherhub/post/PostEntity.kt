package eti.lucasgomes.tetherhub.post

import eti.lucasgomes.tetherhub.dsl.MongoEntity
import eti.lucasgomes.tetherhub.post.PostRepository.Companion.POST_COLLECTION
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@MongoEntity(POST_COLLECTION)
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
