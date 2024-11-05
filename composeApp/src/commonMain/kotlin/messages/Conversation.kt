package messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import profile.User

@Composable
fun Conversation(conversation: Conversation, onClick: () -> Unit) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(users.toString(), style = typography.labelMedium)
            Text(conversation.lastMessage.content, style = typography.bodyMedium)
        }
    }
}

open class Conversation(
    val id: String,
    val users: List<User>,
    val lastMessage: Message
)