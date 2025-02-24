package response

import kotlinx.serialization.Serializable

@Serializable
data class TetherHubError(val httpCode: Int, val internalCode: String, val message: String) {
    val formatedMessage get() = "$internalCode - $message"
}