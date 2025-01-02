package response

import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(val id: String, val author: String, val content: String, val likes: Int)
