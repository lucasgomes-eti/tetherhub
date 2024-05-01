class AndroidPlatform : Platform {
    override val os = OS.ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()