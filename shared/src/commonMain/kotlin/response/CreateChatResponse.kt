package response

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatResponse(val chatId: String, val roomName: String, val users: List<String>)
