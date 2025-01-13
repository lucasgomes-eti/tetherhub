package eti.lucasgomes.tetherhub.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.util.toCharArray
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonObjectId
import org.bson.types.ObjectId

class UserRepository(private val mongoDatabase: MongoDatabase) {
    companion object {
        const val USER_COLLECTION = "user"
    }

    suspend fun insertOne(user: UserEntity): Either<Exception, BsonObjectId> {
        return try {
            mongoDatabase.getCollection<UserEntity>(USER_COLLECTION)
                .insertOne(user).insertedId!!.asObjectId().right()
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun findUserByEmail(email: String): UserEntity? {
        return try {
            mongoDatabase.getCollection<UserEntity>(USER_COLLECTION).withDocumentClass<UserEntity>()
                .find(Filters.eq("email", email))
                .firstOrNull()
        } catch (e: MongoException) {
            null
        }
    }

    suspend fun findUserByCredentials(credentials: EmailPasswordCredentials): UserEntity? {
        val user = findUserByEmail(credentials.email)

        user ?: return null

        val passwordVerificationResult =
            BCrypt.verifyer().verify(credentials.password.toCharArray(), user.passwordHash)

        if (!passwordVerificationResult.verified) {
            return null
        }

        return user
    }

    suspend fun findById(userId: BsonObjectId): UserEntity? {
        return try {
            mongoDatabase.getCollection<UserEntity>(USER_COLLECTION).withDocumentClass<UserEntity>()
                .find(Filters.eq("_id", userId))
                .firstOrNull()
        } catch (e: MongoException) {
            null
        }
    }

    suspend fun findById(userId: ObjectId): UserEntity? {
        return try {
            mongoDatabase.getCollection<UserEntity>(USER_COLLECTION).withDocumentClass<UserEntity>()
                .find(Filters.eq("id", userId))
                .firstOrNull()
        } catch (e: MongoException) {
            null
        }
    }
}