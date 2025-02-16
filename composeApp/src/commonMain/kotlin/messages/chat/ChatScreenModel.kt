package messages.chat

import Message
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import messages.ChatClient
import network.onSuccess

//data class LocalMessage(private val message: Message, val isFromMe: Boolean) :
//    Message(message.id, message.sender, message.content)

class ChatScreenModel(private val chatId: String, private val chatClient: ChatClient) :
    ScreenModel {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            users = emptyList(),
            messages = listOf(Message("chatId: $chatId")),
            ""
        )
    )
    val uiState = _uiState.asStateFlow()

    private var chatListenerJob: Job? = null

    init {
        subscribeToChat()
        fetchChat(chatId)
    }

    private fun fetchChat(chatId: String) {
        screenModelScope.launch {
            chatClient.getChatById(chatId).onSuccess {
                _uiState.update { state -> state.copy(users = it.users) }
            }
        }
    }

    private fun subscribeToChat() {
        chatListenerJob = screenModelScope.launch {
            chatClient.connectToChat(chatId).collect {
                val messages = _uiState.value.messages.toMutableList().apply {
                    add(it)
                }
                _uiState.update { state -> state.copy(messages = messages) }
            }
        }
    }

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
            chatClient.sendMessage(Message(_uiState.value.draft))
            _uiState.update { state -> state.copy(draft = "") }
        }
    }

    override fun onDispose() {
        super.onDispose()
        chatListenerJob?.cancel()
    }
}