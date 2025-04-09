package messages.rooms

sealed class RoomsAction {
    data class OpenNewChat(val chatId: String) : RoomsAction()
    data object CreateNewRoom : RoomsAction()
}