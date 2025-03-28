package profile.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import components.ErrorBanner
import home.LocalNavigationAppBar
import response.PublicProfileResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchProfile(uiState: SearchProfileUiState, onAction: (SearchProfileAction) -> Unit) {
    val navigationAppBar = LocalNavigationAppBar.current
    DisposableEffect(Unit) {
        navigationAppBar.hide()
        onDispose { navigationAppBar.show() }
    }
    val listState = rememberLazyListState()
    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(reachedBottom) {
        if (reachedBottom) onAction(SearchProfileAction.FetchMore)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Searching for: ${uiState.searchQuery}") },
                navigationIcon = {
                    IconButton(onClick = { onAction(SearchProfileAction.NavigateBack) }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                AnimatedVisibility(uiState.errorMessage.isNotBlank()) {
                    ErrorBanner(uiState.errorMessage) { onAction(SearchProfileAction.DismissError) }
                }
            }
            items(uiState.profiles.items) {
                ListItem(
                    headlineContent = { Text(it.username) },
                    supportingContent = {
                        Text(
                            when (it.relationshipStatus) {
                                PublicProfileResponse.RelationshipStatus.FRIENDS -> "You are friends with this user"
                                PublicProfileResponse.RelationshipStatus.NOT_FRIENDS -> "Press invite to request friendship"
                                PublicProfileResponse.RelationshipStatus.PENDING -> "Pending request"
                                PublicProfileResponse.RelationshipStatus.SELF -> "You"
                            }
                        )
                    },
                    trailingContent = if (it.relationshipStatus == PublicProfileResponse.RelationshipStatus.NOT_FRIENDS) {
                        {
                            Button(onClick = { onAction(SearchProfileAction.InviteFriend(it)) }) {
                                Text(
                                    "Invite"
                                )
                            }
                        }
                    } else null,
                )
            }
        }
    }
}