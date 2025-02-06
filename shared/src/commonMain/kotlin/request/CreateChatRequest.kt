package request

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRequest(val roomName: String, val users: List<String>)
