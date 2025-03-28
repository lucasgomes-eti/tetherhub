package response

import kotlinx.serialization.Serializable

@Serializable
data class PublicProfileResponse(
    val id: String,
    val username: String,
    val relationshipStatus: RelationshipStatus,
) {
    @Serializable
    enum class RelationshipStatus {
        SELF,
        NOT_FRIENDS,
        FRIENDS,
        PENDING
    }
}

val PublicProfileResponse.isFriendsWithYou
    get() = relationshipStatus == PublicProfileResponse.RelationshipStatus.FRIENDS