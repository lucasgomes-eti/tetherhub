package feed

import DATE_TIME_PRESENTATION_FORMAT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import response.PostResponse

@Composable
fun Post(post: PostResponse, onPostLiked: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(post.author, style = typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(
                    post.createdAt
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .format(DATE_TIME_PRESENTATION_FORMAT),
                    style = typography.labelSmall
                )
            }
            Text(post.content, style = typography.bodyLarge)
        }
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = onPostLiked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Like", style = typography.bodyMedium)
                BadgedBox(badge = { if (post.likes > 0) Badge { Text(post.likes.toString()) } }) {
                    Icon(
                        if (post.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Like Button"
                    )
                }
            }
        }
    }
}