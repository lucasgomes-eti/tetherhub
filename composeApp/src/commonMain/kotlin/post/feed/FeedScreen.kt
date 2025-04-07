package post.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents

object FeedScreen : Screen {
    @Composable
    override fun Content() {
        val feedScreenModel = koinScreenModel<FeedScreenModel>()
        val feedUiState by feedScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(feedScreenModel.navigationActions)
        Feed(feedUiState, feedScreenModel::onAction)
    }

}