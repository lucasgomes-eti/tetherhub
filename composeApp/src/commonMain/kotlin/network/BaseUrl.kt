package network

data class BaseUrl(
    val protocol: String,
    val host: String,
    val port: Int
) {
    val path: String
        get() = "$protocol$host:$port"
}
