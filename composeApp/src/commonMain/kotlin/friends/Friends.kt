package friends

import DATE_TIME_PRESENTATION_FORMAT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.ErrorBanner
import components.ProfileSearchBar
import dsl.plus
import home.LocalNavigationAppBar
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import response.FriendshipSolicitationResponse
import response.PublicProfileResponse

@Composable
fun Friends(uiState: FriendsUiState, onAction: (FriendsAction) -> Unit) {

    val navigationAppBar = LocalNavigationAppBar.current
    DisposableEffect(Unit) {
        navigationAppBar.hide()
        onDispose { navigationAppBar.show() }
    }

    Scaffold(
        topBar = {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                ProfileSearchBar(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChanged = { onAction(FriendsAction.SearchQueryChanged(it)) },
                    onSearch = { onAction(FriendsAction.Search) },
                    onCancelSearch = { onAction(FriendsAction.CancelSearch) },
                    onNavigateBack = { onAction(FriendsAction.NavigateBack) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding + PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVisibility(uiState.errorMessage.isNotBlank()) {
                    ErrorBanner(uiState.errorMessage) { onAction(FriendsAction.DismissError) }
                }
            }
            item {
                Text("Requests", style = typography.titleMedium)
            }
            items(uiState.requests) {
                FriendRequest(it) { onAction(FriendsAction.AcceptRequest(it.id)) }
            }
            item {
                Text("Friends", style = typography.titleMedium)
            }
            items(uiState.friends) {
                Friend(it)
            }
        }
    }
}

@Composable
fun FriendRequest(friendRequest: FriendshipSolicitationResponse, onAccept: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("From: ${friendRequest.fromUsername}")
        Text(
            "At: ${
                friendRequest.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
                    .format(DATE_TIME_PRESENTATION_FORMAT)
            }"
        )
        Button(onClick = onAccept) {
            Text("Accept")
        }
    }
}

@Composable
fun Friend(publicProfileResponse: PublicProfileResponse) {
    Text(text = publicProfileResponse.username)
}