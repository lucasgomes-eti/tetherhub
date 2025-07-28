package accountOptions

import androidx.compose.ui.text.input.TextFieldValue

data class AccountOptionsUiState(
    val appVersion: String,
    val accountDeletionConfirmationText: TextFieldValue,
    val accountDeletionConfirmationError: Boolean,
    val accountDeletionError: String,
    val isDeleteAccountDialogShown: Boolean,
    val isDeletingAccount: Boolean
)
