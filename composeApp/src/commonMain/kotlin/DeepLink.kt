import kotlinx.serialization.Serializable

@Serializable
data class DeepLink(
    val destination: DeepLinkDestination,
    val resourceId: String
)

@Serializable
enum class DeepLinkDestination { CHAT, FRIENDS }
