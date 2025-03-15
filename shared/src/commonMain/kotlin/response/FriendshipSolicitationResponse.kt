package response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FriendshipSolicitationResponse(
    val id: String,
    val fromUsername: String,
    val createdAt: Instant,
)
