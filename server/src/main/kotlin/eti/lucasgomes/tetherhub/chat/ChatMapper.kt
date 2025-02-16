package eti.lucasgomes.tetherhub.chat

import eti.lucasgomes.tetherhub.user.UserMapper
import eti.lucasgomes.tetherhub.user.UserRepository
import org.bson.types.ObjectId
import request.CreateChatRequest
import response.ChatResponse

class ChatMapper(private val userRepository: UserRepository, private val userMapper: UserMapper) {
    fun buildChatEntity(createChatRequest: CreateChatRequest) = with(createChatRequest) {
        ChatEntity(id = ObjectId(), roomName = roomName, users = users)
    }

    suspend fun fromEntityToResponse(chatEntity: ChatEntity) = with(chatEntity) {
        ChatResponse(
            chatId = id.toString(),
            roomName = roomName,
            users = users.mapNotNull { userId ->
                userRepository.findById(ObjectId(userId))
                    ?.let { userEntity -> userMapper.fromEntityToResponse(userEntity) }
            })
    }
}