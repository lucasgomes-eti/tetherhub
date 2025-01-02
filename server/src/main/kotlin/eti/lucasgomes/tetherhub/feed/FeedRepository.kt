package eti.lucasgomes.tetherhub.feed

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonObjectId
import org.bson.types.ObjectId

class FeedRepository(private val mongoDatabase: MongoDatabase) {
    companion object {
        const val POST_COLLECTION = "post"
    }

    suspend fun insertOne(post: PostEntity): Either<Exception, BsonObjectId> {
        return try {
            mongoDatabase.getCollection<PostEntity>(POST_COLLECTION)
                .insertOne(post).insertedId!!.asObjectId().right()
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun findAll(): List<PostEntity> {
        return try {
            mongoDatabase.getCollection<PostEntity>(POST_COLLECTION).withDocumentClass<PostEntity>()
                .find()
                .toList()
        } catch (e: MongoException) {
            emptyList()
        }
    }

    suspend fun findById(postId: ObjectId): PostEntity? {
        return try {
            mongoDatabase.getCollection<PostEntity>(POST_COLLECTION).withDocumentClass<PostEntity>()
                .find(Filters.eq("_id", postId))
                .firstOrNull()
        } catch (e: MongoException) {
            null
        }
    }
}