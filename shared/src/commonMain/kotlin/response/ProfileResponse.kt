package response

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(val username: String, val email: String, val friendsCount: Int)
