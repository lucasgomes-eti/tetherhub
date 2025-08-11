package eti.lucasgomes.tetherhub.post

import PUBLICATION_WORD_LIMIT
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import eti.lucasgomes.tetherhub.user.UserErrors
import eti.lucasgomes.tetherhub.user.UserRepository
import org.bson.types.ObjectId
import request.CreatePostRequest
import response.PageResponse
import response.PostResponse
import response.TetherHubError

class PostService(
    private val postRepository: PostRepository,
    private val postMapper: PostMapper,
    private val userRepository: UserRepository
) {

    suspend fun savePost(
        createPostRequest: CreatePostRequest,
        authorEmail: String
    ): Either<TetherHubError, PostResponse> =
        either {
            val user = userRepository.findUserByEmail(authorEmail)
            ensure(user != null) { UserErrors.UserNotFoundByEmail(authorEmail) }
            ensure(createPostRequest.content.length <= PUBLICATION_WORD_LIMIT) { PostErrors.PostIsTooLong }
            when (val insertResult =
                postRepository.insertOne(postMapper.buildPost(createPostRequest, user.username))) {
                is Either.Left -> {
                    raise(PostErrors.PostNotCreated(insertResult.value))
                }

                is Either.Right -> {
                    val postResult = postRepository.findById(insertResult.value)
                    ensure(postResult != null) { PostErrors.PostByIdNotFound(insertResult.value.value.toString()) }
                    postMapper.fromEntityToPostResponse(postResult, user.id)
                }
            }
        }

    suspend fun findAll(
        userId: ObjectId,
        page: Int,
        size: Int
    ): Either<TetherHubError, PageResponse<PostResponse>> = either {
        postRepository.findAll(page = page, size = size)
            .mapLeft { PostErrors.ErrorWhileFetchingPosts(it) }.map { pageEntity ->
            PageResponse(
                items = pageEntity.items.map { entity ->
                    postMapper.fromEntityToPostResponse(
                        entity,
                        userId
                    )
                },
                totalPages = pageEntity.totalPages,
                totalItems = pageEntity.totalItems,
                currentPage = pageEntity.currentPage,
                lastPage = pageEntity.lastPage
            )
        }.bind()
    }

    suspend fun findById(postId: ObjectId, userId: ObjectId): Either<TetherHubError, PostResponse> =
        either {
            val post = postRepository.findById(postId)
            ensure(post != null) { PostErrors.PostByIdNotFound(postId.toString()) }
            postMapper.fromEntityToPostResponse(post, userId)
        }

    suspend fun toggleLike(
        postId: ObjectId,
        userId: ObjectId
    ): Either<TetherHubError, PostResponse> = either {
        val post = postRepository.findById(postId)
        ensure(post != null) { PostErrors.PostByIdNotFound(postId.toString()) }
        val updatedLikes = post.likes.toMutableSet().apply {
            if (contains(userId.toString())) remove(userId.toString()) else add(userId.toString())
        }.toList()
        val updatedPost = post.copy(likes = updatedLikes)
        when (val updateResult = postRepository.updateOne(updatedPost)) {
            is Either.Left -> {
                raise(PostErrors.PostNotUpdated(updateResult.value))
            }

            is Either.Right -> {
                ensure(updateResult.value) { PostErrors.PostNotUpdatedWithoutException }
                postMapper.fromEntityToPostResponse(updatedPost, userId)
            }
        }
    }

    suspend fun findPostsByAuthor(userId: ObjectId): List<PostResponse> {
        val user = userRepository.findById(userId) ?: error("User with id: $userId not found!")
        return postRepository.findByAuthor(user.username)
            .map { postMapper.fromEntityToPostResponse(it, userId) }
    }

    suspend fun editContent(
        postId: ObjectId,
        userId: ObjectId,
        newContent: String
    ): Either<TetherHubError, PostResponse> = either {
        val post = postRepository.findById(postId)
        ensure(post != null) { PostErrors.PostByIdNotFound(postId.toString()) }
        val user = userRepository.findById(userId)
        ensure(user != null) { UserErrors.UserNotFound }
        val updatedPost = postMapper.updatePostContent(post, newContent = newContent)
        when (val updateResult = postRepository.updateOne(updatedPost)) {
            is Either.Left -> {
                raise(PostErrors.PostNotUpdated(updateResult.value))
            }

            is Either.Right -> {
                ensure(updateResult.value) { PostErrors.PostNotUpdatedWithoutException }
                postMapper.fromEntityToPostResponse(updatedPost, userId)
            }
        }
    }

    suspend fun deletePost(postId: ObjectId, userId: ObjectId): Either<TetherHubError, Unit> =
        either {
            val post = postRepository.findById(postId)
            ensure(post != null) { PostErrors.PostByIdNotFound(postId.toString()) }
            val user = userRepository.findById(userId)
            ensure(user != null) { UserErrors.UserNotFound }
            ensure(post.author == user.username) { PostErrors.NoPermissionToDelete }
            when (val deleteResult = postRepository.deleteOne(postId)) {
                is Either.Left -> {
                    raise(PostErrors.PostNotDeleted(deleteResult.value))
                }

                is Either.Right -> {
                    ensure(deleteResult.value) { PostErrors.PostNotDeletedWithoutException }
                    Unit
                }
            }
        }
}