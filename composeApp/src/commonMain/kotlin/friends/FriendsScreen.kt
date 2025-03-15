package friends

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents

object FriendsScreen : Screen {

    @Composable
    override fun Content() {
        val friendsScreenModel = koinScreenModel<FriendsScreenModel>()
        val friendsUiState by friendsScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(friendsScreenModel.navigationActions)
        Friends(friendsUiState, friendsScreenModel::onAction)
    }
}