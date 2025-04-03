package friends

sealed class FriendsAction {
    data object CancelSearch : FriendsAction()
    data object NavigateBack : FriendsAction()
    data class SearchQueryChanged(val query: String) : FriendsAction()
    data object Search : FriendsAction()
    data class AcceptRequest(val id: String) : FriendsAction()
    data object DismissError : FriendsAction()
}