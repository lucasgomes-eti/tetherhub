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
}