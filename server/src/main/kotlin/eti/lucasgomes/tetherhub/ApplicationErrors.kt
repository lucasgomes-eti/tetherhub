package eti.lucasgomes.tetherhub

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import response.TetherHubError

object ApplicationErrors {
    @Serializable
    data class Unauthorized(@Transient private val resource: String? = null) : TetherHubError(
        HttpStatusCode.Unauthorized.value,
        "TH-101",
        "You're not authorized to access this resource: $resource"
    )
}