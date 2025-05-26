package eti.lucasgomes.tetherhub.friends

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import eti.lucasgomes.tetherhub.dsl.find
import eti.lucasgomes.tetherhub.dsl.findById
import eti.lucasgomes.tetherhub.dsl.insertOne
import eti.lucasgomes.tetherhub.dsl.withCollection
import org.bson.types.ObjectId

class FriendsRepository(private val mongoDatabase: MongoDatabase) {

    suspend fun insertOne(friendshipSolicitation: FriendshipSolicitationEntity) =
        mongoDatabase.insertOne(friendshipSolicitation)

    suspend fun findById(id: ObjectId) = mongoDatabase.findById<FriendshipSolicitationEntity>(id)

    suspend fun update(entity: FriendshipSolicitationEntity) =
        mongoDatabase.withCollection<FriendshipSolicitationEntity, Boolean> {
            val updates = Updates.combine(
                Updates.set(
                    FriendshipSolicitationEntity::accepted.name,
                    entity.accepted
                )
            )
            updateOne(Filters.eq("id", entity.id), updates).modifiedCount == 1L
        }

    suspend fun findByAssignedToUserId(clientUserId: ObjectId) =
        mongoDatabase.find<FriendshipSolicitationEntity>(
            Filters.and(
                eq(FriendshipSolicitationEntity::toId.name, clientUserId),
                eq(FriendshipSolicitationEntity::accepted.name, false)
            )
        )
}