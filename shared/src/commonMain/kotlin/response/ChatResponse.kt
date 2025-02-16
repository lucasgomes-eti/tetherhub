package response

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(val chatId: String, val roomName: String, val users: List<UserResponse>)
