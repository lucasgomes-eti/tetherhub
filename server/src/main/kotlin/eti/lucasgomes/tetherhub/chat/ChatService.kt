package eti.lucasgomes.tetherhub.chat

import NotificationType
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import eti.lucasgomes.tetherhub.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import request.CreateChatRequest
import response.ChatResponse
import response.MessageResponse
import response.TetherHubError

class ChatService(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val chatMapper: ChatMapper,
    private val firebaseMessaging: FirebaseMessaging,
) {

    suspend fun createChat(createChatRequest: CreateChatRequest): Either<TetherHubError, ChatResponse> =
        either {
            ensure(createChatRequest.users.isNotEmpty()) { ChatErrors.UsersAreEmpty }
            ensure(usersAreInDb(createChatRequest.users.map { ObjectId(it) })) { ChatErrors.UsersNotFoundInDb }
            ensure(createChatRequest.roomName.isNotEmpty()) { ChatErrors.MissingRoomName }
            val entity = chatRepository.insertOne(chatMapper.buildChatEntity(createChatRequest))
                .mapLeft { ChatErrors.ChatCreationError(it) }.flatMap { id ->
                    chatRepository.findById(id).mapLeft { ChatErrors.ChatNotFound(id.toString()) }
                }.bind()
            chatMapper.fromEntityToResponse(entity)
        }

    suspend fun findById(chatId: ObjectId): Either<TetherHubError, ChatResponse> = either {
        val entity =
            chatRepository.findById(chatId).mapLeft { ChatErrors.ChatNotFound(chatId.toString()) }
                .bind()
        chatMapper.fromEntityToResponse(entity)
    }

    private suspend fun usersAreInDb(users: List<ObjectId>): Boolean {
        if (users.isEmpty()) {
            return false
        }
        users.forEach {
            userRepository.findById(it) ?: return false
        }
        return true
    }

    suspend fun findRoomsByUserId(userId: ObjectId): Either<TetherHubError, List<ChatResponse>> =
        either {
            chatRepository.findByUserId(userId).mapLeft { ChatErrors.ErrorWhileFetchingRooms(it) }
                .map { it.map { entity -> chatMapper.fromEntityToResponse(entity) } }.bind()
        }

    suspend fun sendNotification(userId: String, chatId: String, messageResponse: MessageResponse) {
        val fcmToken = userRepository.findById(ObjectId(userId))?.fcmToken ?: return
        withContext(Dispatchers.Default) {
            val data = mapOf(
                "type" to NotificationType.CHAT.name,
                "chatId" to chatId,
                "senderId" to messageResponse.senderId,
                "senderUsername" to messageResponse.senderUsername,
                "content" to messageResponse.content,
                "at" to Json.encodeToString(Instant.serializer(), messageResponse.at),
            )
            val message = Message.builder()
                .setToken(fcmToken)
                .putAllData(data)
                .build()
            firebaseMessaging.send(message)
        }
    }
}