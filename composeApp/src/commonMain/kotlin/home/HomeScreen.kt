package home

import DeepLink
import DeepLinkDestination
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents
import messages.MessagesTab
import post.feed.FeedTab

data class HomeScreen(val deepLink: DeepLink? = null) : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<HomeScreenModel>()
        LaunchedEffect(Unit) {
            viewModel.verifyFcmToken()
        }
        ObserveNavigationEvents(viewModel.navigationActions)
        if (deepLink != null) {
            when (deepLink.destination) {
                DeepLinkDestination.CHAT -> Home(MessagesTab(deepLink))
            }
        } else {
            Home(FeedTab)
        }
    }
}