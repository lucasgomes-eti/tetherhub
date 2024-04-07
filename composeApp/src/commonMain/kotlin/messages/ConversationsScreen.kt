package messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen

object ConversationsScreen : Screen {

    @Composable
    override fun Content() {
        val conversationsScreenModel = rememberScreenModel { ConversationsScreenModel() }
        val conversationsUiState by conversationsScreenModel.uiState.collectAsState()
        Conversations(conversationsUiState)
    }
}