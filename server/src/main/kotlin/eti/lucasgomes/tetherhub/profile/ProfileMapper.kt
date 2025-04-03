package eti.lucasgomes.tetherhub.profile

import eti.lucasgomes.tetherhub.user.UserEntity
import eti.lucasgomes.tetherhub.user.UserRepository
import org.bson.types.ObjectId
import response.ProfileResponse
import response.PublicProfileResponse

class ProfileMapper(private val userRepository: UserRepository) {
    fun transformUserEntityToProfileResponse(userEntity: UserEntity) =
        ProfileResponse(
            username = userEntity.username,
            email = userEntity.email,
            friendsCount = userEntity.friends.size
        )

    suspend fun fromUserEntityToPublicProfile(userEntity: UserEntity, clientUserId: ObjectId) =
        with(userEntity) {
            PublicProfileResponse(
                id = id.toString(),
                username = username,
                relationshipStatus = getRelationshipStatus(this, clientUserId)
            )
        }

    suspend fun fromUserEntityToPublicProfile(
        clientUserId: ObjectId,
        entity: UserEntity
    ): PublicProfileResponse = with(entity) {
        PublicProfileResponse(
            id = id.toString(),
            username = username,
            relationshipStatus = getRelationshipStatus(this, clientUserId)
        )
    }

    private suspend fun getRelationshipStatus(
        userEntity: UserEntity,
        clientUserId: ObjectId
    ): PublicProfileResponse.RelationshipStatus = with(userEntity) {
        return if (friends.contains(clientUserId.toString())) {
            PublicProfileResponse.RelationshipStatus.FRIENDS
        } else {
            if (id == clientUserId)
                return PublicProfileResponse.RelationshipStatus.SELF
            val clientUser = userRepository.findById(clientUserId)
            if (friendRequests.contains(clientUserId.toString()) || clientUser?.friendRequests?.contains(
                    userEntity.id.toString()
                ) == true
            )
                PublicProfileResponse.RelationshipStatus.PENDING
            else
                PublicProfileResponse.RelationshipStatus.NOT_FRIENDS
        }
    }
}
