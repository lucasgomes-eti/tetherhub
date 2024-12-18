package profile

sealed class ProfileAction {
    data object DismissError : ProfileAction()
}
