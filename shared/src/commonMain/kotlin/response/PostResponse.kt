package response

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
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
