package eti.lucasgomes.tetherhub.user

import io.ktor.server.auth.Credential
import kotlinx.serialization.Serializable

@Serializable
data class EmailPasswordCredentials(val email: String, val password: String) : Credential