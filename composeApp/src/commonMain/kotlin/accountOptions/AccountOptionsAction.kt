package accountOptions

sealed class AccountOptionsAction {
    data object Logout : AccountOptionsAction()
    data object NavigateBack : AccountOptionsAction()
}