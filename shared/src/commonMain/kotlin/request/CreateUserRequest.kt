package request

import kotlin.jvm.JvmInline

@JvmInline
value class Email(val value: String) {
    fun isValid(): Boolean {
        return value.matches("[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?".toRegex())
    }
}

@JvmInline
value class Username(val value: String)

@JvmInline
value class Password(val value: String) {
    fun isValid(): Boolean {
        return value.matches("^(?=.*[A-Za-z])(?=.*\\d).+\$".toRegex())
    }
}

data class CreateUserRequest(val email: Email, val username: String, val password: Password)
