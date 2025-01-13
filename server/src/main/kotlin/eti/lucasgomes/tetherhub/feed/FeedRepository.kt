package eti.lucasgomes.tetherhub.feed

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
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
                .sort(Sorts.descending(PostEntity::createdAt.name))
                .toList()
        } catch (e: MongoException) {
            emptyList()
        }
    }

    suspend fun findById(postId: BsonObjectId): PostEntity? {
        return try {
            mongoDatabase.getCollection<PostEntity>(POST_COLLECTION).withDocumentClass<PostEntity>()
                .find(Filters.eq("_id", postId))
                .firstOrNull()
        } catch (e: MongoException) {
            null
        }
    }

    suspend fun findById(postId: ObjectId): PostEntity? {
        return try {
            mongoDatabase.getCollection<PostEntity>(POST_COLLECTION).withDocumentClass<PostEntity>()
                .find(Filters.eq("id", postId))
                .firstOrNull()
        } catch (e: MongoException) {
            null
        }
    }

    suspend fun updateOne(post: PostEntity): Either<Exception, Boolean> {
        return try {
            val updates = Updates.combine(
                Updates.set(PostEntity::content.name, post.content),
                Updates.set(PostEntity::likes.name, post.likes)
            )
            mongoDatabase.getCollection<PostEntity>(POST_COLLECTION)
                .updateOne(Filters.eq("id", post.id), updates).modifiedCount.let {
                    (it == 1L).right()
                }
        } catch (e: Exception) {
            e.left()
        }
    }

    suspend fun findByAuthor(author: String): List<PostEntity> {
        return try {
            mongoDatabase.getCollection<PostEntity>(POST_COLLECTION).withDocumentClass<PostEntity>()
                .find(Filters.eq("author", author))
                .sort(Sorts.descending(PostEntity::createdAt.name))
                .toList()
        } catch (e: MongoException) {
            emptyList()
        }
    }
}