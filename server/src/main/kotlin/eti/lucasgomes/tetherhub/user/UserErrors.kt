package eti.lucasgomes.tetherhub.user

import io.ktor.http.HttpStatusCode
import response.TetherHubError

@Suppress("FunctionName")
object UserErrors {
    val InvalidEmail = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-001",
        "Email doesn't have a valid format"
    )

    val InvalidParameters = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-002",
        "Invalid parameters for this request"
    )

    val EmptyUsername =
        TetherHubError(HttpStatusCode.BadRequest.value, "TH-003", "Username must not be empty")

    val InvalidPassword = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-004",
        "Password must not be empty or have spaces. It must have at least 8 characters composed of letters and numbers"
    )

    val CreateUserError = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-004",
        "Could not create user on database"
    )

    val UserNotFoundAfterCreatingIt = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-005",
        "Could not found the nearly created user on database"
    )

    val UserNotFound = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-006",
        "Could not found the user on database"
    )

    val EmailNotUnique = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-007",
        "This email already exists on database"
    )

    fun UserNotFoundByEmail(email: String) = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-008",
        "Could not found user with this email: $email"
    )

    fun ErrorWhileRegisteringFcmToken(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-009",
        "Error while registering FCM token. Cause: ${cause.message}",
    )

    fun ErrorWhileDeletingPosts(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-010",
        "Error while deleting posts. Cause: ${cause.message}"
    )

    fun ErrorWhileDeletingUser(cause: Exception) = TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-011",
        "Error while deleting user. Cause: ${cause.message}"
    )
}