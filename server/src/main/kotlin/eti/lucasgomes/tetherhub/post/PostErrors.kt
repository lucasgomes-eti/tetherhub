package eti.lucasgomes.tetherhub.post

import PUBLICATION_WORD_LIMIT
import io.ktor.http.HttpStatusCode
import response.TetherHubError

object PostErrors {
    data class PostNotCreated(private val exception: Exception) : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-201",
        "Error while saving the post on the database. Cause: ${exception.message}"
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

    data object PostIsTooLong : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-204",
        "Post is too long, the maximum number of allowed characters is: $PUBLICATION_WORD_LIMIT"
    )

    data class PostNotUpdated(private val exception: Exception) : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-205",
        "Error while updating the post on the database. Cause: ${exception.message}"
    )

    data object PostNotUpdatedWithoutException : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-206",
        "Error while updating the post on the database."
    )

    data class PostNotDeleted(private val exception: Exception) : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-207",
        "Error while deleting the post on the database. Cause: ${exception.message}"
    )

    data object PostNotDeletedWithoutException : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-208",
        "Error while deleting the post on the database."
    )

    data object NoPermissionToDelete : TetherHubError(
        HttpStatusCode.Forbidden.value,
        "TH-209",
        "You can only delete your own post!"
    )
}