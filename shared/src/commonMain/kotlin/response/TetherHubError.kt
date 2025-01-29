package response

import kotlinx.serialization.Serializable

@Serializable
open class TetherHubError(val httCode: Int, val internalCode: String, val message: String) {
    val formatedMessage get() = "$internalCode - $message"
}