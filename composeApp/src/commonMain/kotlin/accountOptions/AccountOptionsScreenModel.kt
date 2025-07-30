package accountOptions

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import network.onError
import network.onSuccess

class AccountOptionsScreenModel(
    private val accountOptionsClient: AccountOptionsClient
) : ScreenModel {

    private var _uiState = MutableStateFlow(
        AccountOptionsUiState(
            appVersion = "",
            accountDeletionConfirmationText = TextFieldValue(),
            accountDeletionConfirmationError = false,
            accountDeletionError = "",
            isDeleteAccountDialogShown = false,
            isDeletingAccount = false
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    fun onAction(action: AccountOptionsAction) {
        when (action) {
            AccountOptionsAction.Logout -> onLogout()
            AccountOptionsAction.NavigateBack -> onNavigateBack()
            AccountOptionsAction.OpenDeleteAccountDialog -> onOpenDeleteAccountDialog()
            AccountOptionsAction.CloseDeleteAccountDialog -> onCloseDeleteAccountDialog()
            AccountOptionsAction.DeleteAccount -> onDeleteAccount()
            is AccountOptionsAction.AccountDeletionConfirmationTextChanged -> onAccountDeletionConfirmationTextChanged(
                action.value
            )

            AccountOptionsAction.DismissError -> onDismissError()
        }
    }

    private fun onDismissError() = withScreenModelScope {
        _uiState.update { state -> state.copy(accountDeletionError = "") }
    }

    private fun onCloseDeleteAccountDialog() = withScreenModelScope {
        _uiState.update { state -> state.copy(isDeleteAccountDialogShown = false) }
    }

    private fun onOpenDeleteAccountDialog() = withScreenModelScope {
        _uiState.update { state -> state.copy(isDeleteAccountDialogShown = true) }
    }

    private fun onDeleteAccount() = withScreenModelScope {
        if (uiState.value.accountDeletionConfirmationText.text != "delete") {
            _uiState.update { state -> state.copy(accountDeletionConfirmationError = true) }
            return@withScreenModelScope
        }
        _uiState.update { state ->
            state.copy(
                isDeleteAccountDialogShown = false,
                isDeletingAccount = true
            )
        }
        accountOptionsClient.deleteAccount().onError {
            _uiState.update { state ->
                state.copy(
                    accountDeletionError = it.formatedMessage,
                    isDeletingAccount = false
                )
            }
        }.onSuccess {
            onLogout()
        }
    }

    private fun onAccountDeletionConfirmationTextChanged(value: TextFieldValue) {
        _uiState.update { state ->
            state.copy(
                accountDeletionConfirmationText = value,
                accountDeletionConfirmationError = false
            )
        }
    }

    private fun onLogout() = withScreenModelScope {
        accountOptionsClient.logOut()
    }

    private fun onNavigateBack() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Pop)
    }
}