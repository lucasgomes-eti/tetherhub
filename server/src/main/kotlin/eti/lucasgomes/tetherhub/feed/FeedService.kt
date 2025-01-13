package eti.lucasgomes.tetherhub.feed

import PUBLICATION_WORD_LIMIT
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import eti.lucasgomes.tetherhub.user.UserErrors
import eti.lucasgomes.tetherhub.user.UserRepository
import org.bson.types.ObjectId
import request.CreatePostRequest
import response.PostResponse
import response.TetherHubError

class FeedService(
    private val feedRepository: FeedRepository,
    private val feedMapper: FeedMapper,
    private val userRepository: UserRepository
) {

    suspend fun savePost(
        createPostRequest: CreatePostRequest,
        authorEmail: String
    ): Either<TetherHubError, PostResponse> =
        either {
            val user = userRepository.findUserByEmail(authorEmail)
            ensure(user != null) { UserErrors.UserNotFoundByEmail(authorEmail) }
            ensure(createPostRequest.content.length <= PUBLICATION_WORD_LIMIT) { FeedErrors.PostIsTooLong }
            when (val insertResult =
                feedRepository.insertOne(feedMapper.buildPost(createPostRequest, user.username))) {
                is Either.Left -> {
                    raise(FeedErrors.PostNotCreated(insertResult.value))
                }

                is Either.Right -> {
                    val postResult = feedRepository.findById(insertResult.value)
                    ensure(postResult != null) { FeedErrors.PostByIdNotFound(insertResult.value.value.toString()) }
                    feedMapper.fromEntityToPostResponse(postResult, user.id)
                }
            }
        }

    suspend fun findAll(userId: ObjectId): List<PostResponse> {
        return feedRepository.findAll().map { feedMapper.fromEntityToPostResponse(it, userId) }
    }

    suspend fun toggleLike(
        postId: ObjectId,
        userId: ObjectId
    ): Either<TetherHubError, PostResponse> = either {
        val post = feedRepository.findById(postId)
        ensure(post != null) { FeedErrors.PostByIdNotFound(postId.toString()) }
        val updatedLikes = post.likes.toMutableSet().apply {
            if (contains(userId.toString())) remove(userId.toString()) else add(userId.toString())
        }.toList()
        val updatedPost = post.copy(likes = updatedLikes)
        when (val updateResult = feedRepository.updateOne(updatedPost)) {
            is Either.Left -> {
                raise(FeedErrors.PostNotUpdated(updateResult.value))
            }

            is Either.Right -> {
                ensure(updateResult.value) { FeedErrors.PostNotUpdatedWithoutException }
                feedMapper.fromEntityToPostResponse(updatedPost, userId)
            }
        }
    }
}

