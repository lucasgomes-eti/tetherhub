package accountOptions

import cafe.adriel.voyager.core.model.ScreenModel
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import network.HttpClientManager

class AccountOptionsScreenModel(
    private val httpClientManager: HttpClientManager
) : ScreenModel {

    private var _uiState = MutableStateFlow(AccountOptionsUiState(""))
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    fun onAction(action: AccountOptionsAction) {
        when (action) {
            AccountOptionsAction.Logout -> onLogout()
            AccountOptionsAction.NavigateBack -> onNavigateBack()
        }
    }

    private fun onLogout() = withScreenModelScope {
        httpClientManager.logOut()
    }

    private fun onNavigateBack() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Pop)
    }
}