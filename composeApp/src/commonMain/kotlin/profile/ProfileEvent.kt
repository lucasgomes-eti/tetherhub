package profile

sealed class ProfileEvent {
    data object None : ProfileEvent()
    data class EditMyPost(val postId: String) : ProfileEvent()
}
