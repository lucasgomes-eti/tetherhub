package post.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

object FeedScreen : Screen {
    @Composable
    override fun Content() {
        val feedScreenModel = koinScreenModel<FeedScreenModel>()
        val feedUiState by feedScreenModel.uiState.collectAsState()
        Feed(feedUiState, feedScreenModel::onAction)
    }

}