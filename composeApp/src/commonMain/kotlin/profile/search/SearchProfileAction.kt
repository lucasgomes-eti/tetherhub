package profile.search

import response.PublicProfileResponse

sealed class SearchProfileAction {
    data object NavigateBack : SearchProfileAction()
    data object FetchMore : SearchProfileAction()
    data object DismissError : SearchProfileAction()
    data class InviteFriend(val profile: PublicProfileResponse) : SearchProfileAction()
}