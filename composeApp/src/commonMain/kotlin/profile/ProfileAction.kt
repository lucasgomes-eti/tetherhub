package profile

sealed class ProfileAction {
    data object DismissError : ProfileAction()
    data class LikeMyPost(val postId: String) : ProfileAction()
}
