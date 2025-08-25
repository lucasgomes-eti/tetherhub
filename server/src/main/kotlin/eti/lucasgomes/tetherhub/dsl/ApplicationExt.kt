package eti.lucasgomes.tetherhub.dsl

import arrow.core.Either
import arrow.core.raise.either
import eti.lucasgomes.tetherhub.user.User
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.routing.RoutingContext
import io.ktor.util.pipeline.PipelineContext
import org.bson.types.ObjectId
import javax.naming.AuthenticationException

val ApplicationCall.userId: ObjectId
    get() = principal<User>()?.id?.value ?: throw AuthenticationException()

val ApplicationCall.userEmail: String
    get() = principal<User>()?.email?.value ?: throw AuthenticationException()

val ApplicationCall.username: String
    get() = principal<User>()?.username?.value ?: throw AuthenticationException()

val PipelineContext<*, ApplicationCall>.userId: ObjectId
    get() = call.userId

val PipelineContext<*, ApplicationCall>.userEmail: String
    get() = call.userEmail

val PipelineContext<*, ApplicationCall>.username: String
    get() = call.username

val RoutingContext.userId: ObjectId
    get() = call.userId

val RoutingContext.userEmail: String
    get() = call.userEmail

val RoutingContext.username: String
    get() = call.username

suspend fun RoutingContext.getParameterAsObjectIdOrRespond(
    key: String,
    response: suspend ApplicationCall.(Exception) -> Unit
): Either<Unit, ObjectId> = either {
    try {
        ObjectId(call.parameters[key])
    } catch (e: Exception) {
        raise(call.response(e))
    }
}