package messages.newroom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import home.LocalNavigationAppBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewRoom(uiState: NewRoomUiState, onAction: (NewRoomAction) -> Unit) {

    val navigationAppBar = LocalNavigationAppBar.current

    DisposableEffect(Unit) {
        navigationAppBar.hide()
        onDispose { navigationAppBar.show() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primaryContainer,
                    titleContentColor = colorScheme.primary,
                ),
                title = { Text("Create new chat") },
                actions = {
                    IconButton(onClick = { onAction(NewRoomAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            "Close"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (uiState.showAllSelected) {
            ModalBottomSheet(
                onDismissRequest = { onAction(NewRoomAction.DismissAllSelected) },
            ) {
                LazyColumn {
                    for (user in uiState.selectedUsers) {
                        item {
                            ListItem(
                                headlineContent = {
                                    Text(user)
                                },
                                trailingContent = {
                                    Button(onClick = {
                                        onAction(NewRoomAction.RemoveUser(user))
                                    }) {
                                        Text("Remove")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.name,
                onValueChange = { onAction(NewRoomAction.NameChanged(it)) },
                label = {
                    Text("Chat name")
                },
                placeholder = {
                    Text("me, ${uiState.selectedUsers.joinToString()}")
                }
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Start a conversation with: ", style = typography.titleMedium)
                AnimatedVisibility(uiState.selectedUsers.isEmpty()) {
                    Text("Select one or more friends from the list!")
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    maxLines = 2,
                    overflow = FlowRowOverflow.expandIndicator {
                        Button(onClick = { onAction(NewRoomAction.ShowAllSelected) }) {
                            Text("Show all")
                        }
                    }
                ) {
                    for (user in uiState.selectedUsers.reversed()) {
                        InputChip(
                            onClick = {
                                onAction(NewRoomAction.RemoveUser(user))
                            },
                            selected = true,
                            label = { Text(user) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null
                                )
                            },
                        )
                    }
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                for (user in uiState.users) {
                    item {
                        val backgroundColor by animateColorAsState(
                            targetValue = if (user.isSelected) colorScheme.primaryContainer else colorScheme.background
                        )
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = backgroundColor),
                            headlineContent = { Text(user.user) },
                            trailingContent = if (user.isSelected) {
                                { Icon(Icons.Default.Check, null) }
                            } else {
                                {
                                    Button(onClick = {
                                        onAction(NewRoomAction.AddUser(user.user))
                                    }) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Add, null)
                                            Text("Add", style = typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                enabled = uiState.selectedUsers.isNotEmpty()
            ) {
                Text("Create chat", style = typography.bodyMedium)
            }
        }
    }
}