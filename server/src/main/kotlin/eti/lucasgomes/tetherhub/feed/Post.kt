package eti.lucasgomes.tetherhub.feed

import org.bson.types.ObjectId

data class PostEntity(val id: ObjectId, val author: String, val content: String, val likes: Int)
