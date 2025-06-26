package post.detail

import PUBLICATION_WORD_LIMIT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import components.ErrorBanner
import home.LocalNavigationAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditPost(
    uiState: CreateOrEditPostUiState,
    onCreatePostAction: (CreateOrEditPostAction) -> Unit
) {

    val navigator = LocalNavigator.currentOrThrow
    val navigationAppBar = LocalNavigationAppBar.current

    DisposableEffect(Unit) {
        navigationAppBar.hide()
        onDispose {
            navigationAppBar.show()
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.primaryContainer,
                titleContentColor = colorScheme.onPrimaryContainer,
            ),
            title = { Text(uiState.topAppBarTitle) },
            actions = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        "Close",
                        tint = colorScheme.onPrimaryContainer
                    )
                }
            })
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                value = uiState.publication,
                onValueChange = { onCreatePostAction(CreateOrEditPostAction.PublicationChanged(it)) },
                label = { Text("Publication") },
                placeholder = { Text("What's in your mind?") },
                supportingText = {
                    if (uiState.publicationFieldError.isNotEmpty()) Text(uiState.publicationFieldError) else Text(
                        "${uiState.publication.length}/${PUBLICATION_WORD_LIMIT}"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default
                ),
                isError = uiState.publicationFieldError.isNotEmpty(),
                minLines = 10
            )
            AnimatedVisibility(uiState.errorMsg.isNotEmpty()) {
                ErrorBanner(uiState.errorMsg) {
                    onCreatePostAction(CreateOrEditPostAction.DismissError)
                }
            }
            Box(Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                onClick = { onCreatePostAction(CreateOrEditPostAction.PublishOrEditPost) }) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(with(LocalDensity.current) { typography.bodyMedium.fontSize.toDp() })
                    )
                } else {
                    Text("Publish", style = typography.bodyMedium)
                }
            }
        }
    }

}