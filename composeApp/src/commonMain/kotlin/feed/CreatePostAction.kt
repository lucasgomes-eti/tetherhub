package feed

sealed class CreatePostAction {
    data class PublicationChanged(val value: String) : CreatePostAction()
    data object PublishPost : CreatePostAction()
    data object DismissError : CreatePostAction()
}