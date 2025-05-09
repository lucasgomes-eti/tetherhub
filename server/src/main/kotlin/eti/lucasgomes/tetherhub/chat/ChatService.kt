package eti.lucasgomes.tetherhub.chat

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import eti.lucasgomes.tetherhub.user.UserRepository
import org.bson.types.ObjectId
import request.CreateChatRequest
import response.ChatResponse
import response.TetherHubError

class ChatService(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val chatMapper: ChatMapper
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
}