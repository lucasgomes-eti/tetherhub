package messages.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import messages.chat.ChatScreen

@Composable
fun Rooms(roomsUiState: RoomsUiState) {

    val navigator = LocalNavigator.currentOrThrow

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(roomsUiState.rooms) {
            RoomItem(it) { navigator.push(ChatScreen) }
        }
    }
}