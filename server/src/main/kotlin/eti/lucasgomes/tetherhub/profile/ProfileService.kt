package eti.lucasgomes.tetherhub.profile

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import eti.lucasgomes.tetherhub.user.UserErrors
import eti.lucasgomes.tetherhub.user.UserRepository
import response.PageResponse
import response.ProfileResponse
import response.PublicProfileResponse
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

    suspend fun getProfilesByUsername(username: String): Either<TetherHubError, PageResponse<PublicProfileResponse>> =
        either {
            ensure(username.isNotBlank()) { ProfileErrors.InvalidUsername }
            userRepository.findUsersByUsername(username, page = 1, size = 20)
                .mapLeft { ProfileErrors.ErrorWhileFetchingProfiles(it) }
                .map {
                    with(it) {
                        PageResponse(
                            items = items.map { userEntity ->
                                profileMapper.fromUserEntityToPublicProfile(
                                    userEntity
                                )
                            },
                            totalPages = totalPages,
                            totalItems = totalItems,
                            currentPage = currentPage,
                            lastPage = lastPage
                        )
                    }
                }.bind()
        }
}

