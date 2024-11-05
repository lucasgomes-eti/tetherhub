package response

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserResponse(val id: String, val email: String, val username: String)
