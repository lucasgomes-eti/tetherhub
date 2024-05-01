package messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen

object ChatScreen : Screen {

    @Composable
    override fun Content() {
        val chatScreenModel = rememberScreenModel { ChatScreenModel() }
        val chatUiState by chatScreenModel.uiState.collectAsState()
        Chat(chatUiState, chatScreenModel::onChatAction)
    }
}