package eti.lucasgomes.tetherhub

import io.ktor.server.auth.UserPasswordCredential

class UserSource {

    private val testUser = ApiUser("1", "scary")

    private val users = listOf(testUser).associateBy(ApiUser::username)
    fun findUserByUsername(username: String): ApiUser? = users[username]

    fun findUserByCredentials(credential: UserPasswordCredential): ApiUser? = users[credential.name]

}