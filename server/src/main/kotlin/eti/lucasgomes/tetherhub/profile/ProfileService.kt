package eti.lucasgomes.tetherhub.profile

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import eti.lucasgomes.tetherhub.user.UserErrors
import eti.lucasgomes.tetherhub.user.UserRepository
import response.ProfileResponse
import response.TetherHubError

class ProfileService(
    private val userRepository: UserRepository,
    private val profileMapper: ProfileMapper
) {

    suspend fun getProfile(email: String): Either<TetherHubError, ProfileResponse> = either {
        val userResult = userRepository.findUserByEmail(email)
        ensure(userResult != null) { UserErrors.UserNotFound }
        profileMapper.transformUserEntityToProfileResponse(userResult)
    }
}

