package post.feed

import DeepLink
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

data class FeedTab(val deepLink: DeepLink? = null) : Tab {
    @Composable
    override fun Content() {
        Navigator(FeedScreen(deepLink))
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