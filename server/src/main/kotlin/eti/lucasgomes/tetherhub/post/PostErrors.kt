package eti.lucasgomes.tetherhub.post

import PUBLICATION_WORD_LIMIT
import io.ktor.http.HttpStatusCode
import response.TetherHubError

@Suppress("FunctionName")
object PostErrors {

    fun PostNotCreated(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-201",
        "Error while saving the post on the database. Cause: ${cause.message}"
    )

    fun PostByIdNotFound(id: String) = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-202",
        "Post with id: $id was not found"
    )

    val InvalidParameters = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-203",
        "Invalid parameters for this request"
    )

    val PostIsTooLong = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-204",
        "Post is too long, the maximum number of allowed characters is: $PUBLICATION_WORD_LIMIT"
    )

    fun PostNotUpdated(exception: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-205",
        "Error while updating the post on the database. Cause: ${exception.message}"
    )

    val PostNotUpdatedWithoutException = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-206",
        "Error while updating the post on the database."
    )

    fun PostNotDeleted(exception: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-207",
        "Error while deleting the post on the database. Cause: ${exception.message}"
    )

    val PostNotDeletedWithoutException = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-208",
        "Error while deleting the post on the database."
    )

    val NoPermissionToDelete = TetherHubError(
        HttpStatusCode.Forbidden.value,
        "TH-209",
        "You can only delete your own post!"
    )

    fun ErrorWhileFetchingPosts(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-210",
        "Error while fetching posts from the database. Cause: ${cause.message}"
    )
}