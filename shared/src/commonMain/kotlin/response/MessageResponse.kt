package response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val senderId: String,
    val senderUsername: String,
    val content: String,
    val at: Instant,
    val type: MessageType
)

@Serializable
enum class MessageType { USER, SYSTEM }
