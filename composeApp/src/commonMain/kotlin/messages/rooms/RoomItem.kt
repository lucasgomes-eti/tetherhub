package messages.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import response.ChatResponse

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomItem(conversation: ChatResponse, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        val users = StringBuilder()
        conversation.users.forEachIndexed { index, user ->
            users.append(user.username)
            if (index != conversation.users.lastIndex) {
                users.append(", ")
            }
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(conversation.roomName, style = typography.bodyMedium)
            HorizontalDivider()
            Text(users.toString(), style = typography.bodyMedium)
        }
    }
}