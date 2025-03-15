package eti.lucasgomes.tetherhub.friends

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import eti.lucasgomes.tetherhub.user.UserErrors
import eti.lucasgomes.tetherhub.user.UserRepository
import org.bson.types.ObjectId
import request.FriendshipSolicitationRequest
import response.FriendshipSolicitationResponse
import response.PublicProfileResponse
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
        val clientUser = userRepository.findById(ObjectId(friendshipSolicitation.to))
        ensure(clientUser != null) { FriendsErrors.UserNotFound }
        friendsRepository.insertOne(friendsMapper.buildEntity(friendshipSolicitation, clientUser))
            .mapLeft { FriendsErrors.ErrorWhileCreatingFriendshipSolicitation(it) }.bind()
    }

    suspend fun acceptFriendRequest(
        friendshipRequestId: ObjectId,
        clientUserId: ObjectId
    ): Either<TetherHubError, Unit> = either {
        friendsRepository.findById(friendshipRequestId).mapLeft {
            FriendsErrors.FriendshipRequestNotFound
        }.flatMap { friendshipSolicitation ->
            ensure(friendshipSolicitation.toId == clientUserId) { FriendsErrors.NotAuthorizedToAccept }
            friendsRepository.update(friendshipSolicitation.copy(accepted = true))
                .mapLeft { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation(it) }
                .map { success ->
                    ensure(success) { FriendsErrors.ErrorWhileAcceptingFriendshipSolicitation }
                    updateUsersFriendsList(friendshipSolicitation)
                }
        }.bind()
    }

    private suspend fun Raise<TetherHubError>.updateUsersFriendsList(friendshipSolicitation: FriendshipSolicitationEntity) {
        val toUser = userRepository.findById(friendshipSolicitation.toId)
        val fromUser = userRepository.findById(friendshipSolicitation.fromId)

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

    suspend fun getFriendsByUser(clientUserId: ObjectId): Either<TetherHubError, List<PublicProfileResponse>> =
        either {
            val user = userRepository.findById(clientUserId)
            ensure(user != null) { UserErrors.UserNotFound }
            val friends = user.friends.mapNotNull { friendId ->
                userRepository.findById(ObjectId(friendId))
            }
            friends.map { friendsMapper.fromUserEntityToPublicProfile(clientUserId, it) }
        }

    suspend fun getFriendshipRequestsByUser(clientUserId: ObjectId): Either<TetherHubError, List<FriendshipSolicitationResponse>> =
        either {
            friendsRepository.findByAssignedToUserId(clientUserId)
                .mapLeft { FriendsErrors.ErrorWhileFetchingFriendRequests }
                .map {
                    it.map { entity ->
                        friendsMapper.fromSolicitationEntityToSolicitationResponse(
                            entity
                        )
                    }
                }.bind()
        }
}