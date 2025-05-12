package request

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(val value: String)
