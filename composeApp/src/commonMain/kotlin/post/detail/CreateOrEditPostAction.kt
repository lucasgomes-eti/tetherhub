package post.detail

sealed class CreateOrEditPostAction {
    data class PublicationChanged(val value: String) : CreateOrEditPostAction()
    data object PublishOrEditPost : CreateOrEditPostAction()
    data object DismissError : CreateOrEditPostAction()
}