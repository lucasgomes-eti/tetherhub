package post.detail

import DATE_TIME_PRESENTATION_FORMAT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import response.PostResponse

@Composable
fun Post(post: PostResponse, onLikeClicked: () -> Unit) {
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
                post.updatedAt?.let { updatedAt ->
                    Text(
                        "Last updated at: ${
                            updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                                .format(DATE_TIME_PRESENTATION_FORMAT)
                        }",
                        style = typography.labelSmall
                    )
                }
            }
            Text(post.content, style = typography.bodyLarge)
        }
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = onLikeClicked) {
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

@Composable
fun MyPost(
    post: PostResponse,
    onLikeClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(contentAlignment = Alignment.TopEnd) {
        Post(post, onLikeClicked)
        Box {
            IconButton({ expanded = true }) {
                Icon(Icons.Default.MoreVert, null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        expanded = false
                        onEditClicked()
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, null) }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        expanded = false
                        onDeleteClicked()
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = colorScheme.error) }
                )
            }
        }

    }
}