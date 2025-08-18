package eti.lucasgomes.tetherhub.friends

import eti.lucasgomes.tetherhub.user.UserEntity
import org.bson.types.ObjectId
import request.FriendshipSolicitationRequest
import response.FriendshipSolicitationResponse
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
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