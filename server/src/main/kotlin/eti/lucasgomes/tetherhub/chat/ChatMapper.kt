package eti.lucasgomes.tetherhub.chat

import org.bson.types.ObjectId
import request.CreateChatRequest
import response.CreateChatResponse

class ChatMapper {
    fun buildChatEntity(createChatRequest: CreateChatRequest) = with(createChatRequest) {
        ChatEntity(id = ObjectId(), roomName = roomName, users = users)
    }

    fun fromEntityToResponse(chatEntity: ChatEntity) = with(chatEntity) {
        CreateChatResponse(chatId = id.toString(), roomName = roomName, users = users)
    }
}