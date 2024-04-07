package feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun Feed(feedUiState: FeedUiState) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(feedUiState.posts) {
            Text(it)
        }
    }
}