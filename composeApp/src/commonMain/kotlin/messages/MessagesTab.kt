package messages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object MessagesTab : Tab {
    @Composable
    override fun Content() {
        val conversationsScreenModel = rememberScreenModel { ConversationsScreenModel() }
        val conversationsUiState by conversationsScreenModel.uiState.collectAsState()
        Conversations(conversationsUiState)
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.AutoMirrored.Filled.Chat)
            val title = "Messages"
            val index: UShort = 1u

            return TabOptions(index, title, icon)
        }
}