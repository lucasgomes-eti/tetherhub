interface Platform {
    val os: OS
}

enum class OS {
    ANDROID, IOS, JVM
}

expect fun getPlatform(): Platform