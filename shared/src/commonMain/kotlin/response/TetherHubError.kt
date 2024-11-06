package response

import kotlinx.serialization.Serializable

@Serializable
open class TetherHubError(val httCode: Int, val internalCode: String, val message: String)