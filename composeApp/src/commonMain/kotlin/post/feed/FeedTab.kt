package post.feed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object FeedTab : Tab {
    @Composable
    override fun Content() {
        Navigator(FeedScreen)
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Filled.ViewTimeline)
            val title = "Feed"
            val index: UShort = 0u

            return TabOptions(index, title, icon)
        }
}