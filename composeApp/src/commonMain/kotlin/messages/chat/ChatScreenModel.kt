package messages.chat

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import profile.User

data class ChatUiState(
    val users: List<User>,
    val messages: List<LocalMessage>,
    val draft: String
)

sealed class ChatAction {
    data class DraftChanged(val value: String) : ChatAction()
    data object Send : ChatAction()
}

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
            ),
            ""
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onChatAction(chatAction: ChatAction) {
        when (chatAction) {
            is ChatAction.DraftChanged -> onDraftChanged(chatAction.value)
            is ChatAction.Send -> onSend()
        }
    }

    private fun onDraftChanged(value: String) {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(draft = value) }
        }
    }

    private fun onSend() {
        screenModelScope.launch {
            if (_uiState.value.draft.isEmpty()) {
                return@launch
            }
            val messages = _uiState.value.messages.toMutableList().apply {
                add(
                    LocalMessage(
                        Message((size + 1).toString(), User("1", "scary"), _uiState.value.draft),
                        true
                    )
                )
            }
            _uiState.update { state -> state.copy(draft = "", messages = messages) }
        }
    }
}