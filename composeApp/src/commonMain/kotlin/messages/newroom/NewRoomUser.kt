package messages.newroom

import response.PublicProfileResponse

data class NewRoomUser(
    val user: PublicProfileResponse,
    val isSelected: Boolean
)