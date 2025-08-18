package eti.lucasgomes.tetherhub.friends

import eti.lucasgomes.tetherhub.dsl.MongoEntity
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@MongoEntity("friendship_solicitations")
data class FriendshipSolicitationEntity(
    val id: ObjectId,
    val fromId: ObjectId,
    val fromUsername: String,
    val toId: ObjectId,
    val createdAt: Instant,
    val accepted: Boolean
)
