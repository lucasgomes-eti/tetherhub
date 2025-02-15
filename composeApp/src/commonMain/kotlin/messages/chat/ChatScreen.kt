package messages.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.koin.core.parameter.ParametersHolder

data class ChatScreen(val chatId: String) : Screen {

    @Composable
    override fun Content() {
        val chatScreenModel = koinScreenModel<ChatScreenModel>() {
            ParametersHolder(mutableListOf(chatId))
        }
        val chatUiState by chatScreenModel.uiState.collectAsState()
        Chat(chatUiState, chatScreenModel::onChatAction)
    }
}