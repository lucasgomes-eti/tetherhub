package profile

sealed class ProfileAction {
    data object DismissError : ProfileAction()
    data class LikeMyPost(val postId: String) : ProfileAction()
    data class DeleteMyPost(val postId: String) : ProfileAction()
    data class EditMyPost(val postId: String) : ProfileAction()
    data object ManageFriends : ProfileAction()
}
