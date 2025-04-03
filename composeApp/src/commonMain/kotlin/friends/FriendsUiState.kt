package friends

import response.FriendshipSolicitationResponse
import response.PublicProfileResponse

data class FriendsUiState(
    val isLoading: Boolean,
    val requests: List<FriendshipSolicitationResponse>,
    val friends: List<PublicProfileResponse>,
    val errorMessage: String,
    val searchQuery: String
)