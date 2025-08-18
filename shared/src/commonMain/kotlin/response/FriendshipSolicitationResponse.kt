package response

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class FriendshipSolicitationResponse(
    val id: String,
    val fromUsername: String,
    val createdAt: Instant,
)
