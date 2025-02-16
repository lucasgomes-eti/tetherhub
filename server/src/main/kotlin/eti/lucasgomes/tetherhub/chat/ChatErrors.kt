package eti.lucasgomes.tetherhub.chat

import io.ktor.http.HttpStatusCode
import response.TetherHubError

object ChatErrors {

    data object UsersAreEmpty : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-301",
        "Can't create chat room without users"
    )

    data object UsersNotFoundInDb : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-302",
        "One or more users weren't found in the database"
    )

    object MissingRoomName : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-303",
        "Missing room name"
    )

    data class ChatCreationError(val exception: Exception) : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-304",
        "Error while creating chat. Cause: ${exception.message}"
    )

    data class ChatNotFound(val id: String) : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-305",
        "Chat with id: $id not found"
    )

    data object InvalidBody : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-306",
        "Invalid body for this request"
    )

    data class ErrorWhileFetchingRooms(val exception: Exception) : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-307",
        "Error while fetching rooms. Cause: ${exception.message}"
    )

    object MissingParameter : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-308",
        "Missing parameter chatId"
    )
}
