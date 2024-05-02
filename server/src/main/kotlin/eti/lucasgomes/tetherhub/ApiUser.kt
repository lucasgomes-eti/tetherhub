package eti.lucasgomes.tetherhub

import io.ktor.server.auth.Principal
import model.User

data class ApiUser(
    override val id: String,
    override val username: String
) : User(id, username), Principal
