package post.feed

import DeepLink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents
import org.koin.core.parameter.parametersOf

data class FeedScreen(val deepLink: DeepLink? = null) : Screen {
    @Composable
    override fun Content() {
        val feedScreenModel = koinScreenModel<FeedScreenModel> { parametersOf(deepLink) }
        val feedUiState by feedScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(feedScreenModel.navigationActions)
        Feed(feedUiState, feedScreenModel::onAction)
    }

}