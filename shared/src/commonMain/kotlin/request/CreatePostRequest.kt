package request

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(val content: String)
