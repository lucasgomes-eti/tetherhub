package eti.lucasgomes.tetherhub.friends

import io.ktor.http.HttpStatusCode
import response.TetherHubError

@Suppress("FunctionName")
object FriendsErrors {

    val InvalidParameters =
        TetherHubError(HttpStatusCode.BadRequest.value, "TH-501", "Invalid parameters")

    val UserNotFound = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-502",
        "The user you are trying to be friends with does not exist in our database"
    )

    fun ErrorWhileCreatingFriendshipSolicitation(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-503",
        "An error occurred while creating your friendship request. Cause: ${cause.message}"
    )

    val FriendshipRequestNotFound =
        TetherHubError(HttpStatusCode.BadRequest.value, "TH-504", "Friendship request not found")

    val NotAuthorizedToAccept =
        TetherHubError(
            HttpStatusCode.Forbidden.value,
            "TH-505",
            "You are not authorized to accept this friendship request"
        )

    fun ErrorWhileAcceptingFriendshipSolicitation(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-506",
        "An error occurred while accepting your friendship request. Cause: ${cause.message}"
    )

    val ErrorWhileAcceptingFriendshipSolicitation = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-507",
        "An error occurred while accepting your friendship request."
    )

    val ErrorWhileFetchingFriendRequests = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-508",
        "An error occurred while fetching your friendship requests."
    )

    val FriendshipRequestIsNotUnique = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-509",
        "There is already a friendship request to this user"
    )
}
