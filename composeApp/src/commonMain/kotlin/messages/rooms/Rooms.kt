package messages.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import home.LocalNavigationAppBar

@Composable
fun Rooms(roomsUiState: RoomsUiState, onAction: (RoomsAction) -> Unit) {

    val navigationAppBar = LocalNavigationAppBar.current

    Scaffold(
        modifier = Modifier.padding(bottom = navigationAppBar.ContainerHeight),
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(RoomsAction.CreateNewRoom) }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(roomsUiState.rooms) {
                RoomItem(it) { onAction(RoomsAction.OpenNewChat(it.chatId)) }
            }
        }
    }

}