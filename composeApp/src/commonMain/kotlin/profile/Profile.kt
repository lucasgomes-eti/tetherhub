package profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.ErrorBanner
import feed.Post

@Composable
fun Profile(profileUiState: ProfileUiState, onProfileAction: (ProfileAction) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AboutMe(profileUiState, onProfileAction)
        }

        item {
            Text("My Posts", style = typography.titleMedium)
        }

        items(profileUiState.myPosts, key = { it.id }) {
            Post(it) { onProfileAction(ProfileAction.LikeMyPost(it.id)) }
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
        AnimatedVisibility(profileUiState.errorMsg.isNotEmpty()) {
            ErrorBanner(profileUiState.errorMsg) {
                onProfileAction(ProfileAction.DismissError)
            }
        }
    }
}