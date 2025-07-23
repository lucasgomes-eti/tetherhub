package accountOptions

import TERMS_AND_PRIVACY_PATH
import THIRD_PARTY_SOFTWARE_PATH
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
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
        onDispose { navigationAppBar.show() }
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