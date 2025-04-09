package messages.newroom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents

object NewRoomScreen : Screen {
    @Composable
    override fun Content() {
        val newRoomScreenModel = koinScreenModel<NewRoomScreenModel>()
        val uiState by newRoomScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(newRoomScreenModel.navigationActions)
        NewRoom(uiState, newRoomScreenModel::onAction)
    }
}