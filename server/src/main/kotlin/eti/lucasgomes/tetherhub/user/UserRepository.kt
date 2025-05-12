package eti.lucasgomes.tetherhub.user

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import eti.lucasgomes.tetherhub.dsl.withCollection
import io.ktor.util.toCharArray
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonObjectId
import org.bson.types.ObjectId
import response.PageResponse
import java.util.regex.Pattern

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

    suspend fun findUsersByUsername(
        username: String,
        page: Int,
        size: Int
    ): Either<Exception, PageResponse<UserEntity>> =
        mongoDatabase.withCollection<UserEntity, PageResponse<UserEntity>> {
            createIndex(Indexes.text(UserEntity::username.name))
            val escapedText = Pattern.quote(username)
            val filter = Filters.regex(UserEntity::username.name, "(?i).*$escapedText.*")
            val totalItems = countDocuments(filter)
            val totalPages = if (totalItems > 0) ((totalItems + size - 1) / size).toInt() else 0
            val skip = (page - 1) * size

            PageResponse(
                items = find(filter).skip(skip).limit(size).toList(),
                totalPages = totalPages,
                totalItems = totalItems,
                currentPage = page,
                lastPage = page >= totalPages
            )
        }

    suspend fun updateUser(user: UserEntity) = mongoDatabase.withCollection<UserEntity, Boolean> {
        val updates = Updates.combine(
            Updates.set(UserEntity::friends.name, user.friends),
            Updates.set(UserEntity::friendRequests.name, user.friendRequests),
            Updates.set(UserEntity::fcmToken.name, user.fcmToken)
        )
        updateOne(Filters.eq("id", user.id), updates).modifiedCount == 1L
    }
}