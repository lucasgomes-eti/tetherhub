package feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun Feed(feedUiState: FeedUiState, onFeedAction: (FeedAction) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(16.dp)) {
        items(feedUiState.posts, key = { it.id }) {
            Post(it) {
                onFeedAction(FeedAction.Like(it.id))
            }
        }
    }
}