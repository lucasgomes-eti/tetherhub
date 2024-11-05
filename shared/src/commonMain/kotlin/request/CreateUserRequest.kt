package request

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class Email(val value: String) {
    fun isValid(): Boolean {
        return value.matches("[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?".toRegex())
    }
}

@JvmInline
@Serializable
value class Username(val value: String)

@JvmInline
@Serializable
value class Password(val value: String) {
    fun isValid(): Boolean {
        return value.matches("^(?=.*[A-Za-z])(?=.*\\d).+\$".toRegex())
    }
}

@Serializable
data class CreateUserRequest(val email: Email, val username: Username, val password: Password)
