package feed

import LocalEventBus
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.ErrorBanner
import home.LocalNavigationAppBar

@Composable
fun Feed(feedUiState: FeedUiState, onFeedAction: (FeedAction) -> Unit) {

    val navigationAppBar = LocalNavigationAppBar.current
    val navigator = LocalNavigator.currentOrThrow
    val eventBus = LocalEventBus.current

    eventBus.subscribe<PostCreated> {
        onFeedAction(FeedAction.Refresh)
    }

    Scaffold(
        modifier = Modifier.padding(bottom = navigationAppBar.ContainerHeight),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigator.push(CreatePostScreen)
            }) {
                Icon(Icons.Default.Add, null)
            }
        }) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 88.dp)
        ) {
            item {
                AnimatedVisibility(feedUiState.isLoading) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            item {
                AnimatedVisibility(feedUiState.errorMsg.isNotEmpty()) {
                    ErrorBanner(feedUiState.errorMsg) {
                        onFeedAction(FeedAction.DismissError)
                    }
                }
            }

            // TODO: implement pull to refresh
            item {
                Button(onClick = { onFeedAction(FeedAction.Refresh) }) {
                    Text("Refresh")
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