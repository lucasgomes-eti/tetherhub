package eti.lucasgomes.tetherhub.user

import at.favre.lib.crypto.bcrypt.BCrypt
import org.bson.types.ObjectId
import request.CreateUserRequest
import response.CreateUserResponse

class UserMapper(private val userRepository: UserRepository) {

    fun buildUserDomain(userRequest: CreateUserRequest): User {
        val passwordHash =
            BCrypt.withDefaults().hashToString(12, userRequest.password.value.toCharArray())
        return User(
            id = UserId(ObjectId()),
            email = Email(userRequest.email.value),
            username = Username(userRequest.username),
            passwordHash = PasswordHash(passwordHash)
        )
    }

    suspend fun buildCreateUserResponse(userId: ObjectId): CreateUserResponse? {
        val user = userRepository.findById(userId)
        user ?: return null
        return user.run { CreateUserResponse(id.toString(), email, username) }
    }
}

fun User.toEntity() = UserEntity(
    id = id.value,
    email = email.value,
    username = username.value,
    passwordHash = passwordHash.value
)

fun UserEntity.toDomain() = User(
    id = UserId(id),
    email = Email(email),
    username = Username(username),
    passwordHash = PasswordHash(passwordHash)
)