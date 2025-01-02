package eti.lucasgomes.tetherhub.feed

import org.bson.types.ObjectId
import request.CreatePostRequest
import response.PostResponse

class FeedMapper {
    fun buildPost(request: CreatePostRequest, author: String) =
        PostEntity(id = ObjectId(), author = author, content = request.content, likes = 0)

    fun fromEntityToPostResponse(postEntity: PostEntity) = PostResponse(
        id = postEntity.id.toString(),
        author = postEntity.author,
        content = postEntity.content,
        likes = postEntity.likes
    )
}