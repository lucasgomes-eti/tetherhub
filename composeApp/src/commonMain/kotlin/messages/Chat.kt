package messages

import KeyboardEvents
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import getKeyboardHandler
import home.LocalNavigationAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(chatUiState: ChatUiState, onChatAction: (ChatAction) -> Unit) {

    val navigationAppBar = LocalNavigationAppBar.current
    val navigator = LocalNavigator.currentOrThrow

    val keyboardHandler = getKeyboardHandler()

    var isKeyboardOpened by remember { mutableStateOf(false) }
    var keyboardHeight by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        navigationAppBar.hide()
        keyboardHandler.keyboardEvents = object : KeyboardEvents {
            override fun onShow(height: Int) {
                isKeyboardOpened = true
                keyboardHeight = height
            }

            override fun onHide() {
                isKeyboardOpened = false
            }
        }
        keyboardHandler.registerObserver()
        onDispose {
            navigationAppBar.show()
            keyboardHandler.removeObserver()
        }
    }

    val users = StringBuilder()
    chatUiState.users.forEachIndexed { index, user ->
        users.append(user.username)
        if (index != chatUiState.users.lastIndex) {
            users.append(", ")
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val screenHeight = 860.dp // TODO: get the actual screen height by platform

    val scaffoldHeight by animateDpAsState(if (isKeyboardOpened) screenHeight - with(LocalDensity.current) { keyboardHeight.toDp() } else screenHeight)

    Scaffold(
        modifier = Modifier.height(scaffoldHeight).background(Color.Blue),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primaryContainer,
                    titleContentColor = colorScheme.primary,
                ),
                title = { Text(keyboardHeight.toString()/*users.toString()*/) },
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
            item {
                Text(isKeyboardOpened.toString())
            }
            items(chatUiState.messages, key = { it.id }) {
                Message(it)
            }
        }
    }
}