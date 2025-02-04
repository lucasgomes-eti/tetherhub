package eti.lucasgomes.tetherhub.dsl

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonObjectId
import org.bson.types.ObjectId
import kotlin.reflect.KClass

fun <Entity : Any> getCollectionNameByEntityClass(kClass: KClass<Entity>): String {
    val annotation = kClass.annotations.find { it is MongoEntity } as? MongoEntity
    return annotation?.collectionName
        ?: throw RuntimeException("${kClass.simpleName} must ne annotated with @MongoEntity")
}

inline fun <reified Entity : Any, Return> MongoDatabase.withCollection(
    block: MongoCollection<Entity>.() -> Return
): Either<Exception, Return> {
    return try {
        getCollection<Entity>(getCollectionNameByEntityClass(Entity::class))
            .block().right()
    } catch (e: Exception) {
        e.left()
    }
}

suspend inline fun <reified E : Any> MongoDatabase.insertOne(entity: E) =
    withCollection<E, BsonObjectId> {
        insertOne(entity).insertedId!!.asObjectId()
    }

suspend inline fun <reified E : Any> MongoDatabase.findById(id: ObjectId) = withCollection<E, E> {
    withDocumentClass<E>()
        .find(Filters.eq(id::class.simpleName!!, id))
        .firstOrNull()!!
}