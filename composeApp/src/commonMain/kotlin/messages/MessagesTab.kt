package messages

import DeepLink
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import messages.rooms.RoomsScreen

data class MessagesTab(val deepLink: DeepLink? = null) : Tab {
    @Composable
    override fun Content() {
        Navigator(RoomsScreen(deepLink))
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