package post.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.ErrorBanner
import components.ProfileSearchBar
import home.LocalNavigationAppBar
import post.detail.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Feed(feedUiState: FeedUiState, onFeedAction: (FeedAction) -> Unit) {

    val navigationAppBar = LocalNavigationAppBar.current

    LaunchedEffect(Unit) {
        onFeedAction(FeedAction.Created)
    }

    Scaffold(
        modifier = Modifier.padding(bottom = navigationAppBar.ContainerHeight),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onFeedAction(FeedAction.CreatePost)
            }) {
                Icon(Icons.Default.Add, null)
            }
        },
        topBar = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileSearchBar(
                    searchQuery = feedUiState.searchQuery,
                    onSearchQueryChanged = { onFeedAction(FeedAction.SearchQueryChanged(it)) },
                    onSearch = { onFeedAction(FeedAction.Search) },
                    onCancelSearch = { onFeedAction(FeedAction.CancelSearch) },
                )
                IconButton(onClick = { onFeedAction(FeedAction.NavigateToFriends) }) {
                    Icon(Icons.Default.People, null)
                }
            }
        },
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            isRefreshing = feedUiState.isLoading,
            onRefresh = { onFeedAction(FeedAction.Refresh) }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 0.dp,
                    end = 16.dp,
                    bottom = 88.dp
                )
            ) {
                item {
                    AnimatedVisibility(feedUiState.errorMsg.isNotEmpty()) {
                        ErrorBanner(feedUiState.errorMsg) {
                            onFeedAction(FeedAction.DismissError)
                        }
                    }
                }

                items(feedUiState.posts, key = { it.id }) {
                    Post(it) {
                        onFeedAction(FeedAction.Like(it.id))
                    }
                }
            }
        }

    }

}