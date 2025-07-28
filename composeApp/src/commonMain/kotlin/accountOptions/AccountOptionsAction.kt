package accountOptions

import androidx.compose.ui.text.input.TextFieldValue

sealed class AccountOptionsAction {
    data object Logout : AccountOptionsAction()
    data object NavigateBack : AccountOptionsAction()
    data object OpenDeleteAccountDialog : AccountOptionsAction()
    data object CloseDeleteAccountDialog : AccountOptionsAction()
    data object DeleteAccount : AccountOptionsAction()
    data class AccountDeletionConfirmationTextChanged(val value: TextFieldValue) :
        AccountOptionsAction()

    data object DismissError : AccountOptionsAction()
}