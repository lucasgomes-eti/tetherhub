package eti.lucasgomes.tetherhub.user

import kotlinx.serialization.Serializable

@Serializable
data class EmailPasswordCredentials(val email: String, val password: String)