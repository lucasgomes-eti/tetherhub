package eti.lucasgomes.tetherhub.profile

import eti.lucasgomes.tetherhub.user.UserEntity
import org.bson.types.ObjectId
import response.ProfileResponse
import response.PublicProfileResponse

class ProfileMapper {
    fun transformUserEntityToProfileResponse(userEntity: UserEntity) =
        ProfileResponse(username = userEntity.username, email = userEntity.email)

    fun fromUserEntityToPublicProfile(userEntity: UserEntity, clientUserId: ObjectId) =
        with(userEntity) {
            PublicProfileResponse(
                username = username,
                isFriendsWithYou = friends.contains(clientUserId.toString())
            )
        }
}
