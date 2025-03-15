package profile.search

sealed class SearchProfileAction {
    data object NavigateBack : SearchProfileAction()
    data object FetchMore : SearchProfileAction()
    data object DismissError : SearchProfileAction()
}