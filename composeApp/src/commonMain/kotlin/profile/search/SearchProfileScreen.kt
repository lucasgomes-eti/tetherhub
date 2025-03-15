package profile.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dsl.navigation.ObserveNavigationEvents
import org.koin.core.parameter.ParametersHolder

data class SearchProfileScreen(val searchQuery: String) : Screen {

    @Composable
    override fun Content() {
        val searchProfileScreenModel = koinScreenModel<SearchProfileScreenModel> {
            ParametersHolder(mutableListOf(searchQuery))
        }
        val searchProfileUiState by searchProfileScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(searchProfileScreenModel.navigationActions)
        SearchProfile(searchProfileUiState, searchProfileScreenModel::onAction)
    }
}