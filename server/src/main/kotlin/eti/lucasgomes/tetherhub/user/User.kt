package eti.lucasgomes.tetherhub.user

import eti.lucasgomes.tetherhub.dsl.MongoEntity
import eti.lucasgomes.tetherhub.user.UserRepository.Companion.USER_COLLECTION
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@JvmInline
value class UserId(@BsonId val value: ObjectId)

@JvmInline
value class Email(@BsonId val value: String)

@JvmInline
value class Username(@BsonId val value: String)

@JvmInline
value class PasswordHash(@BsonId val value: String)

//domain
data class User(
    @BsonId
    val id: UserId,
    val email: Email,
    val username: Username,
    val passwordHash: PasswordHash
)

//db
@MongoEntity(USER_COLLECTION)
@Serializable
data class UserEntity(
    @Contextual
    val id: ObjectId,
    val email: String,
    val username: String,
    val passwordHash: String,
    @Serializable
    val friends: List<String>,
    @Serializable
    val friendRequests: List<String>,
    val fcmToken: String? = null
)