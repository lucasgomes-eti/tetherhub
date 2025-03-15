package profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val profileScreenModel = koinScreenModel<ProfileScreenModel>()
        val profileUiState by profileScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(profileScreenModel.navigationActions)
        Profile(profileUiState, profileScreenModel::onAction)
    }
}