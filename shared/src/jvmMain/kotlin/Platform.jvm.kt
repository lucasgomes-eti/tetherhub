class JVMPlatform : Platform {
    override val os = OS.JVM
}

actual fun getPlatform(): Platform = JVMPlatform()