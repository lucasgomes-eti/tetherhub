package messages.chat

sealed class ChatAction {
    data class DraftChanged(val value: String) : ChatAction()
    data object Send : ChatAction()
}