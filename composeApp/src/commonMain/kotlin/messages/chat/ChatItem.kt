package messages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import home.LocalNavigationAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(chatUiState: ChatUiState, onChatAction: (ChatAction) -> Unit) {

    val navigationAppBar = LocalNavigationAppBar.current
    val navigator = LocalNavigator.currentOrThrow

    DisposableEffect(Unit) {
        navigationAppBar.hide()
        onDispose {
            navigationAppBar.show()
        }
    }

    val users = StringBuilder()
    chatUiState.users.forEachIndexed { index, user ->
        users.append(user.takeLast(3))
        if (index != chatUiState.users.lastIndex) {
            users.append(", ")
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primaryContainer,
                    titleContentColor = colorScheme.primary,
                ),
                title = { Text(users.toString()) },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            "Navigate back"
                        )
                    }
                })
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth().background(colorScheme.surfaceContainerLow),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = chatUiState.draft,
                        onValueChange = { onChatAction(ChatAction.DraftChanged(it)) },
                        placeholder = { Text("Type something") },
                    )
                    FloatingActionButton(
                        onClick = {
                            keyboardController?.hide()
                            onChatAction(ChatAction.Send)
                        },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            }
        }) { innerPadding ->
        val lazyListState = rememberLazyListState(chatUiState.messages.lastIndex)

        LaunchedEffect(chatUiState.messages) {
            if (lazyListState.canScrollForward) {
                lazyListState.scrollToItem(chatUiState.messages.lastIndex)
            }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxHeight().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
            contentPadding = PaddingValues(16.dp),
        ) {
            items(chatUiState.messages) {
                ChatItem(it)
            }
        }
    }
}