package messages

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.Message
import model.User

data class ChatUiState(
    val users: List<User>,
    val messages: List<LocalMessage>
)

data class LocalMessage(private val message: Message, val isFromMe: Boolean) :
    Message(message.id, message.sender, message.content)

class ChatScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            users = listOf(
                User("1", "scary"),
                User("2", "terry")
            ),
            messages = listOf(
                LocalMessage(Message("1", User("1", "scary"), "let's do smt later"), true),
                LocalMessage(Message("2", User("2", "terry"), "see you on the park"), false)
            )
        )
    )
    val uiState = _uiState.asStateFlow()
}