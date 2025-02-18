package request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(val value: String)
