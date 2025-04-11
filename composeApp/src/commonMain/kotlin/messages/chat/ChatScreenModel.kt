package messages.chat

import DATE_TIME_PRESENTATION_FORMAT
import DataStoreKeys
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dsl.withScreenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import messages.ChatClient
import messages.chat.data.MessageRepository
import network.onSuccess
import request.MessageRequest
import response.MessageType

class ChatScreenModel(
    private val chatId: String,
    private val chatClient: ChatClient,
    private val preferences: DataStore<Preferences>,
    private val messageRepository: MessageRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            users = emptyList(),
            messages = emptyList(),
            ""
        )
    )
    val uiState = _uiState.asStateFlow()

    private var chatListenerJob: Job? = null
    private var _userId: String? = null

    init {
        fetchChat(chatId)
        subscribeToChat()
    }

    private suspend fun getUserId(): String {
        val userIdSnapshot = _userId
        if (userIdSnapshot == null) {
            val userIdFromStorage =
                preferences.data.map { it[stringPreferencesKey(DataStoreKeys.USER_ID)] }
                    .firstOrNull() ?: error("UserId is null")
            _userId = userIdFromStorage
            return userIdFromStorage
        } else {
            return userIdSnapshot
        }
    }

    private fun fetchChat(chatId: String) = withScreenModelScope {
        chatClient.getChatById(chatId).onSuccess {
            _uiState.update { state -> state.copy(users = it.users) }
        }
        val messages = _uiState.value.messages + messageRepository.getMessages(chatId).map {
            LocalMessage(
                content = it.content,
                timeStamp = Instant.fromEpochMilliseconds(it.at)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .format(DATE_TIME_PRESENTATION_FORMAT),
                sender = if (it.senderId == getUserId())
                    MessageSender.ME
                else
                    MessageSender.OTHER,
                senderUsername = it.senderUsername
            )
        }
        _uiState.update { state -> state.copy(messages = messages) }
    }

    private fun subscribeToChat() {
        chatListenerJob = screenModelScope.launch {
            val userId = getUserId()
            chatClient.connectToChat(chatId).collect {
                val messages = _uiState.value.messages.toMutableList().apply {
                    add(
                        LocalMessage(
                            content = it.content,
                            timeStamp = it.at.toLocalDateTime(TimeZone.currentSystemDefault())
                                .format(DATE_TIME_PRESENTATION_FORMAT),
                            sender = if (it.type == MessageType.SYSTEM) {
                                MessageSender.SYSTEM
                            } else {
                                if (it.senderId == userId)
                                    MessageSender.ME
                                else
                                    MessageSender.OTHER
                            },
                            senderUsername = it.senderUsername
                        )
                    )
                }
                _uiState.update { state -> state.copy(messages = messages) }
                if (it.type == MessageType.USER) {
                    messageRepository.saveMessage(it, chatId)
                }
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
            chatClient.sendMessage(MessageRequest(_uiState.value.draft))
            _uiState.update { state -> state.copy(draft = "") }
        }
    }

    override fun onDispose() {
        super.onDispose()
        chatListenerJob?.cancel()
    }
}