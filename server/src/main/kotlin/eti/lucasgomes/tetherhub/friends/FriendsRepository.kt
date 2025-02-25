package eti.lucasgomes.tetherhub.friends

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import eti.lucasgomes.tetherhub.dsl.insertOne

class FriendsRepository(private val mongoDatabase: MongoDatabase) {

    suspend fun insertOne(friendshipSolicitation: FriendshipSolicitationEntity) =
        mongoDatabase.insertOne(friendshipSolicitation)
}