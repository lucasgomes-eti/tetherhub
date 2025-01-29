package request

import kotlinx.serialization.Serializable

@Serializable
data class PatchPostContentRequest(val content: String)
