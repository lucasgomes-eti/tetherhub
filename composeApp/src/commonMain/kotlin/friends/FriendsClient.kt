package friends

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import network.EmptyResult
import network.HttpClientManager
import request.FriendshipSolicitationRequest
import response.FriendshipSolicitationResponse
import response.PublicProfileResponse

class FriendsClient(private val httpClientManager: HttpClientManager) {

    suspend fun getFriends() = httpClientManager.withApiResource<List<PublicProfileResponse>> {
        get("friends")
    }

    suspend fun getFriendRequests() =
        httpClientManager.withApiResource<List<FriendshipSolicitationResponse>> {
            get("friends/requests")
        }

    suspend fun postFriendshipRequest(request: FriendshipSolicitationRequest) =
        httpClientManager.withApiResource<EmptyResult> {
            post("friends/requests") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun acceptFriendshipRequest(requestId: String) =
        httpClientManager.withApiResource<Unit> {
            post("friends/requests/${requestId}/accept")
        }
}