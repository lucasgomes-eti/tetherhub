package request

import kotlinx.serialization.Serializable

@Serializable
data class FriendshipSolicitationRequest(val to: String)
