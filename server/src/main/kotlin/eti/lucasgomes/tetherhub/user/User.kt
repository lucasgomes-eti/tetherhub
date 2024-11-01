package eti.lucasgomes.tetherhub.user

import io.ktor.server.auth.*
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
) : Principal

//db
data class UserEntity(
    val id: ObjectId,
    val email: String,
    val username: String,
    val passwordHash: String
)