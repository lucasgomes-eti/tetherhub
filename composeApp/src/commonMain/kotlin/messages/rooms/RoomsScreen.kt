package messages.rooms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

object RoomsScreen : Screen {

    @Composable
    override fun Content() {
        val roomsScreenModel = koinScreenModel<RoomsScreenModel>()
        val conversationsUiState by roomsScreenModel.uiState.collectAsState()
        Rooms(conversationsUiState)
    }
}