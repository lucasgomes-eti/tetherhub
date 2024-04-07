package feed

sealed class FeedAction {
    data class Like(val postId: String) : FeedAction()
}