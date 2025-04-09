package messages.newroom

import response.PublicProfileResponse

data class NewRoomUiState(
    val name: String,
    val users: List<NewRoomUser>,
    val selectedUsers: List<PublicProfileResponse>,
    val showAllSelected: Boolean,
    val errorMessage: String,
    val isCreating: Boolean,
    val myUsername: String
)