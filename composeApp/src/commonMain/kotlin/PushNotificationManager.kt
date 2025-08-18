import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import messages.chat.data.MessageRepository
import response.MessageResponse
import response.MessageType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PushNotificationManager(
    private val messageRepository: MessageRepository
) {

    suspend fun onPushNotificationWithData(
        notificationType: NotificationType,
        data: Map<String, String>
    ) {
        when (notificationType) {
            NotificationType.CHAT -> handleChatNotification(data)
            NotificationType.FRIENDS -> Unit
        }
    }

    private suspend fun handleChatNotification(data: Map<String, String>) {
        val senderId = data["senderId"] ?: return
        val senderUsername = data["senderUsername"] ?: return
        val content = data["content"] ?: return
        val at = try {
            Json.decodeFromString(Instant.serializer(), data["at"]!!)
        } catch (_: Exception) {
            return
        }
        val chatId = data["chatId"] ?: return
        messageRepository.saveMessage(
            MessageResponse(
                senderId = senderId,
                senderUsername = senderUsername,
                content = content,
                at = at,
                type = MessageType.USER
            ), chatId
        )
    }
}