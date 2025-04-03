package friends

import DATE_TIME_PRESENTATION_FORMAT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import dsl.plus
import home.LocalNavigationAppBar
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import response.FriendshipSolicitationResponse
import response.PublicProfileResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Friends(uiState: FriendsUiState, onAction: (FriendsAction) -> Unit) {
    var isSearchBarFocused by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val navigationAppBar = LocalNavigationAppBar.current
    DisposableEffect(Unit) {
        navigationAppBar.hide()
        onDispose { navigationAppBar.show() }
    }

    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier.padding(16.dp).fillMaxWidth()
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = 0f
                    },
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.onFocusChanged { isSearchBarFocused = it.isFocused },
                        query = uiState.searchQuery,
                        onQueryChange = { onAction(FriendsAction.SearchQueryChanged(it)) },
                        onSearch = { onAction(FriendsAction.Search) },
                        expanded = false,
                        onExpandedChange = { },
                        placeholder = { Text("Search profiles by username") },
                        trailingIcon = {
                            IconButton(onClick = { onAction(FriendsAction.Search) }) {
                                Icon(Icons.Default.Search, null)
                            }
                        },
                        leadingIcon = {
                            if (isSearchBarFocused) {
                                IconButton(onClick = {
                                    onAction(FriendsAction.CancelSearch)
                                    focusManager.clearFocus()
                                }) {
                                    Icon(Icons.Default.Close, null)
                                }
                            } else {
                                IconButton(onClick = { onAction(FriendsAction.NavigateBack) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                }
                            }
                        },
                    )
                },
                expanded = false,
                onExpandedChange = { },
            ) {}
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding + PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Requests", style = typography.titleMedium)
            }
            items(uiState.requests) {
                FriendRequest(it) {}
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