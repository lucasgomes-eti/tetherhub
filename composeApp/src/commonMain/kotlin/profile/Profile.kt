package profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.ErrorBanner
import post.detail.MyPost

@Composable
fun Profile(profileUiState: ProfileUiState, onProfileAction: (ProfileAction) -> Unit) {
    val openAlertDialog = remember { mutableStateOf(DeleteDialogData(false, "")) }
    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    AboutMe(profileUiState, onProfileAction)
                }

                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onProfileAction(ProfileAction.ManageFriends) }) {
                        Text("Manage Friends")
                    }
                }

                item {
                    Text("My Posts", style = typography.titleMedium)
                }

                items(profileUiState.myPosts, key = { it.id }) {
                    MyPost(
                        post = it,
                        onLikeClicked = { onProfileAction(ProfileAction.LikeMyPost(it.id)) },
                        onEditClicked = { onProfileAction(ProfileAction.EditMyPost(it.id)) },
                        onDeleteClicked = { openAlertDialog.value = DeleteDialogData(true, it.id) },
                    )
                }
            }
            DeletePostDialog(openAlertDialog) {
                onProfileAction(ProfileAction.DeleteMyPost(openAlertDialog.value.postId))
            }
        }
    }

}

@Composable
fun AboutMe(profileUiState: ProfileUiState, onProfileAction: (ProfileAction) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(profileUiState.isLoading) {
            CircularProgressIndicator()
        }
        Text(
            "User",
            style = typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Text(
            profileUiState.username,
            style = typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Email",
            style = typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Text(
            profileUiState.email,
            style = typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Friends",
            style = typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Text(
            profileUiState.friendsCount.toString(),
            style = typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        AnimatedVisibility(profileUiState.errorMsg.isNotEmpty()) {
            ErrorBanner(profileUiState.errorMsg) {
                onProfileAction(ProfileAction.DismissError)
            }
        }
    }
}

@Composable
private fun DeletePostDialog(
    openAlertDialog: MutableState<DeleteDialogData>,
    onDeleteConfirmed: () -> Unit
) {
    when {
        openAlertDialog.value.isShown -> {
            AlertDialog(
                title = {
                    Text(text = "Delete Post?")
                },
                text = {
                    Text(text = "You can't undo the deletion!")
                },
                onDismissRequest = {
                    openAlertDialog.value = openAlertDialog.value.copy(isShown = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteConfirmed()
                            openAlertDialog.value = openAlertDialog.value.copy(isShown = false)
                        }
                    ) {
                        Text("Delete", color = colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openAlertDialog.value = openAlertDialog.value.copy(isShown = false)
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private data class DeleteDialogData(val isShown: Boolean, val postId: String)