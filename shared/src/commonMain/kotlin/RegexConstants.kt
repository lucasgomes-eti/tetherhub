object RegexConstants {
    val EMAIL = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()
    val USERNAME = "^[a-z0-9_!@#\$%^&*()-+=]+\$".toRegex()
    val PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d).+\$".toRegex()
}