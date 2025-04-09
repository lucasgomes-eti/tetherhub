package messages.newroom

import response.PublicProfileResponse

sealed class NewRoomAction {
    data object NavigateBack : NewRoomAction()
    data class NameChanged(val value: String) : NewRoomAction()
    data class AddUser(val user: PublicProfileResponse) : NewRoomAction()
    data class RemoveUser(val user: PublicProfileResponse) : NewRoomAction()
    data object ShowAllSelected : NewRoomAction()
    data object DismissAllSelected : NewRoomAction()
    data object DismissError : NewRoomAction()
    data object CreateChat : NewRoomAction()
}