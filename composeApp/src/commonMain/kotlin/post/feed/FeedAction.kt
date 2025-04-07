package post.feed

sealed class FeedAction {
    data class Like(val postId: String) : FeedAction()
    data object DismissError : FeedAction()
    data object Refresh : FeedAction()
    data class SearchQueryChanged(val query: String) : FeedAction()
    data object Search : FeedAction()
    data object CancelSearch : FeedAction()
    data object CreatePost : FeedAction()
    data object NavigateToFriends : FeedAction()
}