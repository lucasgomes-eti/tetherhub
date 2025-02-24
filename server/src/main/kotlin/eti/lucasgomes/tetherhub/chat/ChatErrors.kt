package eti.lucasgomes.tetherhub.chat

import io.ktor.http.HttpStatusCode
import response.TetherHubError

@Suppress("FunctionName")
object ChatErrors {
    val UsersAreEmpty = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-301",
        "Can't create chat room without users"
    )

    val UsersNotFoundInDb = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-302",
        "One or more users weren't found in the database"
    )

    val MissingRoomName = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-303",
        "Missing room name"
    )

    fun ChatCreationError(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-304",
        "Error while creating chat. Cause: ${cause.message}"
    )

    fun ChatNotFound(id: String) = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-305",
        "Chat with id: $id not found"
    )

    val InvalidBody = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-306",
        "Invalid body for this request"
    )

    fun ErrorWhileFetchingRooms(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-307",
        "Error while fetching rooms. Cause: ${cause.message}"
    )

    val MissingParameter = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-308",
        "Missing parameter chatId"
    )
}
