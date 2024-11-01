package eti.lucasgomes.tetherhub.user

import io.ktor.server.auth.Credential

data class EmailPasswordCredentials(val email: String, val password: String) : Credential