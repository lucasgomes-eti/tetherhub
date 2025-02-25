package eti.lucasgomes.tetherhub.friends

import kotlinx.datetime.Clock
import org.bson.types.ObjectId
import request.FriendshipSolicitationRequest

class FriendsMapper {
    fun buildEntity(
        friendshipSolicitation: FriendshipSolicitationRequest,
        clientUserId: ObjectId
    ): FriendshipSolicitationEntity {
        return FriendshipSolicitationEntity(
            id = ObjectId(),
            from = clientUserId,
            to = ObjectId(friendshipSolicitation.to),
            createdAt = Clock.System.now(),
            accepted = false
        )
    }
}