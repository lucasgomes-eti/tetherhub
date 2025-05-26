package messages.rooms

import DeepLink
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents
import org.koin.core.parameter.parametersOf

data class RoomsScreen(val deepLink: DeepLink? = null) : Screen {

    @Composable
    override fun Content() {
        val roomsScreenModel = koinScreenModel<RoomsScreenModel> { parametersOf(deepLink) }
        val conversationsUiState by roomsScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(roomsScreenModel.navigationActions)
        Rooms(conversationsUiState, roomsScreenModel::onAction)
    }
}