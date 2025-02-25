package eti.lucasgomes.tetherhub.friends

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import eti.lucasgomes.tetherhub.user.UserRepository
import org.bson.types.ObjectId
import request.FriendshipSolicitationRequest
import response.TetherHubError

class FriendsService(
    private val friendsRepository: FriendsRepository,
    private val friendsMapper: FriendsMapper,
    private val userRepository: UserRepository
) {

    suspend fun createFriendshipSolicitation(
        friendshipSolicitation: FriendshipSolicitationRequest,
        clientUserId: ObjectId
    ): Either<TetherHubError, Unit> = either {
        ensure(friendshipSolicitation.to.isNotBlank()) { FriendsErrors.UserNotFound }
        ensure(requiredUserExists(friendshipSolicitation.to)) { FriendsErrors.UserNotFound }
        friendsRepository.insertOne(friendsMapper.buildEntity(friendshipSolicitation, clientUserId))
            .mapLeft { FriendsErrors.ErrorWhileCreatingFriendshipSolicitation(it) }.bind()
    }

    private suspend fun requiredUserExists(to: String): Boolean {
        return userRepository.findById(ObjectId(to)) != null
    }
}