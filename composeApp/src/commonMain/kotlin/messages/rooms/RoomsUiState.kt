package messages.rooms

import messages.rooms.data.LocalRoom

data class RoomsUiState(
    val rooms: List<LocalRoom>,
    val isLoading: Boolean,
    val errorMessage: String
)