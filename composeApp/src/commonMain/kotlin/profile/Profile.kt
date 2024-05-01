package profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Profile(profileUiState: ProfileUiState) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("User", style = typography.labelMedium)
        Text(profileUiState.username, style = typography.bodyLarge)
        Spacer(Modifier.height(16.dp))
        Text("Email", style = typography.labelMedium)
        Text(profileUiState.email, style = typography.bodyLarge)
    }
}