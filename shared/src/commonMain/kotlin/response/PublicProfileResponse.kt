package response

import kotlinx.serialization.Serializable

@Serializable
data class PublicProfileResponse(val username: String)
