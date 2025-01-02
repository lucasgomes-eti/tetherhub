package eti.lucasgomes.tetherhub.feed

import io.ktor.http.HttpStatusCode
import response.TetherHubError

object FeedErrors {
    data class PostNotCreated(private val exception: Exception) : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-201",
        "Error while saving the user on the database. Cause: ${exception.message}"
    )

    data class PostByIdNotFound(private val id: String) : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-202",
        "Post with id: $id was not found"
    )

    data object InvalidParameters : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-203",
        "Invalid parameters for this request"
    )
}