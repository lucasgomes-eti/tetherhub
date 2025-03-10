package response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val userId: String, val token: String, val refreshToken: String)
