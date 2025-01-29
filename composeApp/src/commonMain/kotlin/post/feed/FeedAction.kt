package post.feed

sealed class FeedAction {
    data class Like(val postId: String) : FeedAction()
    data object DismissError : FeedAction()
    data object Refresh : FeedAction()
}