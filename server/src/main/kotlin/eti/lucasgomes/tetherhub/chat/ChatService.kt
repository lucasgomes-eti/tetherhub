package eti.lucasgomes.tetherhub.chat

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import eti.lucasgomes.tetherhub.user.UserRepository
import org.bson.types.ObjectId
import request.CreateChatRequest
import response.CreateChatResponse
import response.TetherHubError

class ChatService(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val chatMapper: ChatMapper
) {

    suspend fun createChat(createChatRequest: CreateChatRequest): Either<TetherHubError, CreateChatResponse> =
        either {
            ensure(createChatRequest.users.isNotEmpty()) { ChatErrors.UsersAreEmpty }
            ensure(usersAreInDb(createChatRequest.users.map { ObjectId(it) })) { ChatErrors.UsersNotFoundInDb }
            ensure(createChatRequest.roomName.isNotEmpty()) { ChatErrors.MissingRoomName }
            val entity = chatRepository.insertOne(chatMapper.buildChatEntity(createChatRequest))
                .mapLeft { ChatErrors.ChatCreationError(it) }.flatMap { id ->
                    chatRepository.findById(id).mapLeft { ChatErrors.ChatNotFound(id.toString()) }
                }.bind()
            chatMapper.fromEntityToResponse(entity)
//            when (val insertResult =
//                chatRepository.insertOne(chatMapper.buildChatEntity(createChatRequest))) {
//                is Either.Left -> {
//                    raise(ChatErrors.ChatCreationError(insertResult.value))
//                }
//
//                is Either.Right -> {
//                    when (val entity = chatRepository.findById(insertResult.value)) {
//                        is Either.Left -> {
//                            raise(ChatErrors.ChatNotFound(insertResult.toString()))
//                        }
//
//                        is Either.Right -> {
//                            chatMapper.fromEntityToResponse(entity.value)
//                        }
//                    }
//                }
//            }
        }

    suspend fun findById(chatId: ObjectId): Either<TetherHubError, CreateChatResponse> = either {
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
}