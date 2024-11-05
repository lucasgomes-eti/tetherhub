package messages

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import profile.User

data class ConversationsUiState(
    val conversations: List<Conversation>
)

class ConversationsScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(
        ConversationsUiState(
            listOf(
                Conversation(
                    "1",
                    users = listOf(User("1", "scary"), User("2", "terry")),
                    lastMessage = Message("1", User("2", "terry"), "see you on the park")
                )
            )
        )
    )
    val uiState = _uiState.asStateFlow()
}