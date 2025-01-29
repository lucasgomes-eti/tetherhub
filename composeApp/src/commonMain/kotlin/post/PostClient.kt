package post

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import network.EmptyResult
import network.HttpClientManager
import network.Resource
import request.CreatePostRequest
import request.PatchPostContentRequest
import response.PostResponse

class PostClient(private val httpClientManager: HttpClientManager) {
    suspend fun getPosts(): Resource<List<PostResponse>> = httpClientManager.withApiResource {
        get("/posts")
    }

    suspend fun getPostById(postId: String): Resource<PostResponse> =
        httpClientManager.withApiResource {
            get("posts/$postId")
        }

    suspend fun publishPost(createPostRequest: CreatePostRequest): Resource<PostResponse> =
        httpClientManager.withApiResource {
            post("/posts") {
                contentType(ContentType.Application.Json)
                setBody(createPostRequest)
            }
        }

    suspend fun toggleLike(postId: String): Resource<PostResponse> =
        httpClientManager.withApiResource {
            post("/posts/$postId/toggle_like")
        }

    suspend fun getMyPosts(): Resource<List<PostResponse>> = httpClientManager.withApiResource {
        get("/posts/my_posts")
    }

    suspend fun deleteMyPost(postId: String): EmptyResult = httpClientManager.withApiResource {
        delete("/posts/$postId")
    }

    suspend fun updateContent(postId: String, newContent: String): Resource<PostResponse> =
        httpClientManager.withApiResource {
            patch("/posts/$postId") {
                contentType(ContentType.Application.Json)
                setBody(PatchPostContentRequest(newContent))
            }
        }
}