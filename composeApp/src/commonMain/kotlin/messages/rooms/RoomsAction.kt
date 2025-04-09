package messages.rooms

sealed class RoomsAction {
    data class OpenNewChat(val chatId: String) : RoomsAction()
    data object CreateNewRoom : RoomsAction()
    data object Refresh : RoomsAction()
    data object DismissError : RoomsAction()
}