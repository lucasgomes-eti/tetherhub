package eti.lucasgomes.tetherhub.friends

import eti.lucasgomes.tetherhub.user.UserEntity
import kotlinx.datetime.Clock
import org.bson.types.ObjectId
import request.FriendshipSolicitationRequest
import response.FriendshipSolicitationResponse

class FriendsMapper {
    fun buildEntity(
        friendshipSolicitation: FriendshipSolicitationRequest,
        clientUser: UserEntity
    ): FriendshipSolicitationEntity {
        return FriendshipSolicitationEntity(
            id = ObjectId(),
            fromId = clientUser.id,
            fromUsername = clientUser.username,
            toId = ObjectId(friendshipSolicitation.to),
            createdAt = Clock.System.now(),
            accepted = false
        )
    }

    fun fromSolicitationEntityToSolicitationResponse(entity: FriendshipSolicitationEntity) =
        with(entity) {
            FriendshipSolicitationResponse(
                id = id.toString(),
                fromUsername = fromUsername,
                createdAt = createdAt
            )
        }
}