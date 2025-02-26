package eti.lucasgomes.tetherhub.friends

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.Raise
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

    suspend fun acceptFriendRequest(
        friendshipRequestId: ObjectId,
        clientUserId: ObjectId
    ): Either<TetherHubError, Unit> = either {
        friendsRepository.findById(friendshipRequestId).mapLeft {
            FriendsErrors.FriendshipRequestNotFound
        }.flatMap { friendshipSolicitation ->
            ensure(friendshipSolicitation.to == clientUserId) { FriendsErrors.NotAuthorizedToAccept }
            friendsRepository.update(friendshipSolicitation.copy(accepted = true))
                .mapLeft { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation(it) }
                .map { success ->
                    ensure(success) { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation }
                    updateUsersFriendsList(friendshipSolicitation)
                }
        }.bind()
    }

    private suspend fun Raise<TetherHubError>.updateUsersFriendsList(friendshipSolicitation: FriendshipSolicitationEntity) {
        val toUser = userRepository.findById(friendshipSolicitation.to)
        val fromUser = userRepository.findById(friendshipSolicitation.from)

        ensure(toUser != null) { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation }
        ensure(fromUser != null) { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation }

        val toUserUpdated = userRepository.updateUser(
            toUser.copy(
                friends = toUser.friends.toMutableSet().apply {
                    add(
                        fromUser.id.toString()
                    )
                }.toList()
            )
        ).mapLeft { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation(it) }.bind()
        val fromUserUpdated = userRepository.updateUser(
            fromUser.copy(
                friends = fromUser.friends.toMutableSet().apply {
                    add(
                        toUser.id.toString()
                    )
                }.toList()
            )
        ).mapLeft { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation(it) }.bind()

        ensure(toUserUpdated) { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation }
        ensure(fromUserUpdated) { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation }
    }
}