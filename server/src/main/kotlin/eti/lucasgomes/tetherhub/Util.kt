package eti.lucasgomes.tetherhub

import eti.lucasgomes.tetherhub.user.User
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.util.pipeline.PipelineContext
import javax.naming.AuthenticationException

val PipelineContext<*, ApplicationCall>.userEmail: String
    get() = call.principal<User>()?.email?.value ?: throw AuthenticationException()
