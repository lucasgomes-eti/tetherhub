package accountOptions

import TERMS_AND_PRIVACY_PATH
import THIRD_PARTY_SOFTWARE_PATH
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import components.ErrorBanner
import dsl.update
import home.LocalNavigationAppBar
import network.BaseUrl
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountOptions(uiState: AccountOptionsUiState, onAction: (AccountOptionsAction) -> Unit) {
    val uriHandler = LocalUriHandler.current
    val baseUrl = koinInject<BaseUrl>()
    val navigationAppBar = LocalNavigationAppBar.current
    val logoutDialogDataState = remember { mutableStateOf(LogoutDialogData(false)) }

    DisposableEffect(Unit) {
        navigationAppBar.hide()
        onDispose {
            navigationAppBar.show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primaryContainer,
                    titleContentColor = colorScheme.onPrimaryContainer,
                ),
                title = { Text("Account Options") },
                navigationIcon = {
                    IconButton(onClick = { onAction(AccountOptionsAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            "Close",
                            tint = colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    logoutDialogDataState.update { copy(isShown = true) }
                }, Modifier.fillMaxWidth()) {
                    Text("Logout")
                }
                AnimatedVisibility(visible = uiState.accountDeletionError.isNotEmpty()) {
                    ErrorBanner(uiState.accountDeletionError) { onAction(AccountOptionsAction.DismissError) }
                }
                Button(
                    onClick = {
                        onAction(AccountOptionsAction.OpenDeleteAccountDialog)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.error,
                        contentColor = colorScheme.onError
                    ),
                    enabled = uiState.isDeletingAccount.not()
                ) {
                    if (uiState.isDeletingAccount) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(with(LocalDensity.current) { typography.bodyMedium.fontSize.toDp() })
                        )
                    } else {
                        Text("Delete My Account")
                    }
                }
                HorizontalDivider()
                TextButton(onClick = {
                    uriHandler.openUri("${baseUrl.path}$TERMS_AND_PRIVACY_PATH")
                }, Modifier.fillMaxWidth()) {
                    Text("Terms of Use and Privacy Policy")
                }
                TextButton(onClick = {
                    uriHandler.openUri("${baseUrl.path}$THIRD_PARTY_SOFTWARE_PATH")
                }, Modifier.fillMaxWidth()) {
                    Text("Third-Party Software")
                }
            }

        }
        LogoutDialog(logoutDialogDataState) {
            onAction(AccountOptionsAction.Logout)
        }
        if (uiState.isDeleteAccountDialogShown) {
            DeleteAccountDialog(
                confirmationText = uiState.accountDeletionConfirmationText,
                onConfirmationTextChanged = {
                    onAction(
                        AccountOptionsAction.AccountDeletionConfirmationTextChanged(
                            it
                        )
                    )
                },
                confirmationError = uiState.accountDeletionConfirmationError,
                onCancel = {
                    onAction(AccountOptionsAction.CloseDeleteAccountDialog)
                },
                onDeleteAccountConfirmed = {
                    onAction(AccountOptionsAction.DeleteAccount)
                }
            )
        }
    }
}

private data class LogoutDialogData(val isShown: Boolean)

@Composable
private fun LogoutDialog(
    logoutDialogDataState: MutableState<LogoutDialogData>,
    onLogoutConfirmed: () -> Unit
) {
    if (logoutDialogDataState.value.isShown) {
        AlertDialog(
            title = { Text("Logout?") },
            text = { Text("You're going to be redirected to the login screen.") },
            onDismissRequest = {
                logoutDialogDataState.update { copy(isShown = false) }
            },
            dismissButton = {
                TextButton(onClick = {
                    logoutDialogDataState.update { copy(isShown = false) }
                }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onLogoutConfirmed()
                    logoutDialogDataState.update { copy(isShown = false) }
                }, colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.error)) {
                    Text("Logout")
                }
            }
        )
    }
}

@Composable
private fun DeleteAccountDialog(
    confirmationText: TextFieldValue,
    onConfirmationTextChanged: (TextFieldValue) -> Unit,
    confirmationError: Boolean,
    onCancel: () -> Unit,
    onDeleteAccountConfirmed: () -> Unit
) {

    AlertDialog(
        title = { Text("Delete Account?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("This is a destructive action, deleting your account will wipe all your posts and friends information. If you wish to continue type 'delete' in the box bellow.")
                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    TextField(
                        value = confirmationText,
                        onValueChange = {
                            val newText = it.text
                            val newSelection = TextRange(newText.length)
                            onConfirmationTextChanged(TextFieldValue(newText, newSelection))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.None,
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Done
                        ),
                        isError = confirmationError
                    )
                }
            }
        },
        onDismissRequest = onCancel,
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDeleteAccountConfirmed()
            }, colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.error)) {
                Text("Delete My Account")
            }
        }
    )
}