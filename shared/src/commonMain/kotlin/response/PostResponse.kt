package response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val id: String,
    val author: String,
    val content: String,
    val likes: Int,
    val createdAt: Instant,
    val isLiked: Boolean,
    val updatedAt: Instant?
)
