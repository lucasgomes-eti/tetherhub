package eti.lucasgomes.tetherhub.user

import eti.lucasgomes.tetherhub.exception.TetherHubError
import io.ktor.http.HttpStatusCode

object UserErrors {
    data object InvalidEmail : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-001",
        "Email doesn't have a valid format"
    )

    data object InvalidParameters : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-002",
        "Invalid parameters for this request"
    )

    data object EmptyUsername :
        TetherHubError(HttpStatusCode.BadRequest.value, "TH-003", "Username must not be empty")

    data object InvalidPassword : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-004",
        "Password must not be empty or have spaces. It must have at least 8 characters composed of letters and numbers"
    )

    data object CreateUserError : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-004",
        "Could not create user on database"
    )

    data object UserNotFoundAfterCreatingIt : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-005",
        "Could not found the nearly created user on database"
    )

    data object UserNotFound : TetherHubError(
        HttpStatusCode.InternalServerError.value,
        "TH-006",
        "Could not found the user on database"
    )

    data object EmailNotUnique : TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-007",
        "This email already exists on database"
    )
}