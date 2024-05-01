class IOSPlatform : Platform {
    override val os = OS.IOS
}

actual fun getPlatform(): Platform = IOSPlatform()