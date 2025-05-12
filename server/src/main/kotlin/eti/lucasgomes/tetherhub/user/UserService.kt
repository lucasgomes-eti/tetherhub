package eti.lucasgomes.tetherhub.user

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import org.bson.types.ObjectId
import request.CreateUserRequest
import request.FcmTokenRequest
import response.TetherHubError
import response.UserResponse

class UserService(private val userRepository: UserRepository, private val userMapper: UserMapper) {

    suspend fun saveUser(createUserRequest: CreateUserRequest): Either<TetherHubError, UserResponse> =
        either {
            ensure(createUserRequest.email.isValid()) { UserErrors.InvalidEmail }
            ensure(emailIsUnique(createUserRequest.email.value)) { UserErrors.EmailNotUnique }
            ensure(createUserRequest.username.value.isNotEmpty()) { UserErrors.EmptyUsername }
            ensure(createUserRequest.password.isValid()) { UserErrors.InvalidPassword }


            when (val insertUserResult =
                userRepository.insertOne(
                    userMapper.buildUserDomain(createUserRequest).toEntity()
                )) {
                is Either.Left -> raise(UserErrors.CreateUserError)
                is Either.Right -> {
                    val createdUserResponse =
                        userMapper.buildCreateUserResponse(insertUserResult.value)
                    createdUserResponse ?: raise(UserErrors.UserNotFoundAfterCreatingIt)
                    createdUserResponse
                }
            }
        }

    suspend fun findUserByCredentials(credentials: EmailPasswordCredentials): Either<TetherHubError, User> =
        either {
            val userResult = userRepository.findUserByCredentials(credentials)
            ensure(userResult != null) { UserErrors.UserNotFound }
            userResult.toDomain()
        }

    private suspend fun emailIsUnique(email: String): Boolean =
        userRepository.findUserByEmail(email) == null

    suspend fun registerFcmToken(
        clientUserId: ObjectId,
        fcmToken: FcmTokenRequest
    ): Either<TetherHubError, Unit> = either {
        userRepository.findById(clientUserId)?.let { userEntity ->
            userRepository.updateUser(userEntity.copy(fcmToken = fcmToken.value))
                .mapLeft { UserErrors.ErrorWhileRegisteringFcmToken(it) }.bind()
        } ?: raise(UserErrors.UserNotFound)
    }
}