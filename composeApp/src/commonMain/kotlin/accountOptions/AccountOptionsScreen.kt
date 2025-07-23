package accountOptions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents

object AccountOptionsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<AccountOptionsScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        ObserveNavigationEvents(screenModel.navigationActions)
        AccountOptions(uiState, screenModel::onAction)
    }
}