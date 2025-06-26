package messages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import messages.chat.data.LocalMessage

@Composable
fun ChatItem(message: LocalMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = when (message.sender) {
            MessageSender.ME -> Arrangement.End
            MessageSender.OTHER -> Arrangement.Start
            MessageSender.SYSTEM -> Arrangement.Center
        }
    ) {
        Column(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = when (message.sender) {
                            MessageSender.ME -> 48f
                            MessageSender.OTHER -> 0f
                            MessageSender.SYSTEM -> 48f
                        },
                        bottomEnd = when (message.sender) {
                            MessageSender.ME -> 0f
                            MessageSender.OTHER -> 48f
                            MessageSender.SYSTEM -> 48f
                        }

                    )
                )
                .background(
                    when (message.sender) {
                        MessageSender.ME -> colorScheme.primary
                        MessageSender.OTHER -> colorScheme.surface
                        MessageSender.SYSTEM -> colorScheme.tertiaryContainer
                    }
                )
                .padding(16.dp),
            horizontalAlignment = when (message.sender) {
                MessageSender.ME -> Alignment.End
                MessageSender.OTHER -> Alignment.Start
                MessageSender.SYSTEM -> Alignment.CenterHorizontally
            },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (message.sender != MessageSender.SYSTEM) {
                Text(message.senderUsername, style = typography.labelSmall)
            }
            Text(message.content, style = typography.bodyLarge)
            Text(message.timeStamp, style = typography.labelSmall)
        }
    }
}