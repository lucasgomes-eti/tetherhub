package eti.lucasgomes.tetherhub.post

import kotlinx.datetime.Clock
import org.bson.types.ObjectId
import request.CreatePostRequest
import response.PostResponse

class PostMapper {
    fun buildPost(request: CreatePostRequest, author: String) =
        PostEntity(
            id = ObjectId(),
            author = author,
            content = request.content,
            likes = emptyList(),
            createdAt = Clock.System.now(),
            updatedAt = null
        )

    fun fromEntityToPostResponse(postEntity: PostEntity, userId: ObjectId) = PostResponse(
        id = postEntity.id.toString(),
        author = postEntity.author,
        content = postEntity.content,
        likes = postEntity.likes.size,
        createdAt = postEntity.createdAt,
        isLiked = postEntity.likes.contains(userId.toString()),
        updatedAt = postEntity.updatedAt
    )

    fun updatePostContent(postEntity: PostEntity, newContent: String) =
        postEntity.copy(content = newContent, updatedAt = Clock.System.now())
}