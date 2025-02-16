package response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val token: String, val expiresAt: Long, val userId: String)
