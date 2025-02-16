package response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(val id: String, val email: String, val username: String)
