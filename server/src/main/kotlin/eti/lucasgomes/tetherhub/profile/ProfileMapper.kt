package eti.lucasgomes.tetherhub.profile

import eti.lucasgomes.tetherhub.user.UserEntity
import response.ProfileResponse

class ProfileMapper {
    fun transformUserEntityToProfileResponse(userEntity: UserEntity) =
        ProfileResponse(username = userEntity.username, email = userEntity.email)
}
