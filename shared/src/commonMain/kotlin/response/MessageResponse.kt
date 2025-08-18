package response

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
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
