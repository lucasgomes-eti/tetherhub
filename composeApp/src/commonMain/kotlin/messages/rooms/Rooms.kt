package messages.rooms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.ErrorBanner
import home.LocalNavigationAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Rooms(roomsUiState: RoomsUiState, onAction: (RoomsAction) -> Unit) {

    val navigationAppBar = LocalNavigationAppBar.current

    LaunchedEffect(Unit) {
        onAction(RoomsAction.Refresh)
    }

    Scaffold(
        modifier = Modifier.padding(bottom = navigationAppBar.ContainerHeight),
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(RoomsAction.CreateNewRoom) }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            isRefreshing = roomsUiState.isLoading,
            onRefresh = { onAction(RoomsAction.Refresh) }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    AnimatedVisibility(roomsUiState.errorMessage.isNotBlank()) {
                        ErrorBanner(roomsUiState.errorMessage) { onAction(RoomsAction.DismissError) }
                    }
                }
                items(roomsUiState.rooms) {
                    RoomItem(it) { onAction(RoomsAction.OpenNewChat(it.chat.chatId)) }
                }
            }
        }
    }

}