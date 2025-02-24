package eti.lucasgomes.tetherhub.profile

import io.ktor.http.HttpStatusCode
import response.TetherHubError

@Suppress("FunctionName")
object ProfileErrors {
    val MissingUsernameQueryFilter = TetherHubError(
        httpCode = HttpStatusCode.BadRequest.value,
        "TH-401",
        "Missing username for search"
    )

    val InvalidUsername = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-402",
        "Username is blank"
    )

    fun ErrorWhileFetchingProfiles(cause: Exception) = TetherHubError(
        HttpStatusCode.BadRequest.value,
        "TH-403",
        "Error while fetching profiles. Cause: ${cause.message}"
    )
}