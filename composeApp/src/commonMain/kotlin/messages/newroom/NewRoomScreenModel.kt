package messages.newroom

import cafe.adriel.voyager.core.model.ScreenModel
import dsl.navigation.NavigationAction
import dsl.withScreenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

data class LocalUser(
    val user: String,
    val isSelected: Boolean
)

data class NewRoomUiState(
    val name: String,
    val users: List<LocalUser>,
    val selectedUsers: List<String>,
    val showAllSelected: Boolean,
)

sealed class NewRoomAction {
    data object NavigateBack : NewRoomAction()
    data class NameChanged(val value: String) : NewRoomAction()
    data class AddUser(val user: String) : NewRoomAction()
    data class RemoveUser(val user: String) : NewRoomAction()
    data object ShowAllSelected : NewRoomAction()
    data object DismissAllSelected : NewRoomAction()
}

class NewRoomScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(
        NewRoomUiState(
            name = "",
            users = List(100) { LocalUser("Friend $it", false) },
            selectedUsers = emptyList(),
            showAllSelected = false
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationActions = Channel<NavigationAction>()
    val navigationActions = _navigationActions.receiveAsFlow()

    fun onAction(action: NewRoomAction) {
        when (action) {
            NewRoomAction.NavigateBack -> onNavigateBack()
            is NewRoomAction.NameChanged -> onNameChanged(action.value)
            is NewRoomAction.AddUser -> onAddUser(action.user)
            is NewRoomAction.RemoveUser -> onRemoveUser(action.user)
            NewRoomAction.ShowAllSelected -> onShowAllSelected()
            NewRoomAction.DismissAllSelected -> onDismissAllSelected()
        }
    }

    private fun onDismissAllSelected() = withScreenModelScope {
        _uiState.update { state -> state.copy(showAllSelected = false) }
    }

    private fun onShowAllSelected() = withScreenModelScope {
        _uiState.update { state -> state.copy(showAllSelected = true) }
    }

    private fun onRemoveUser(user: String) = withScreenModelScope {
        _uiState.update { state ->
            state.copy(
                selectedUsers = state.selectedUsers - user,
                users = state.users.toMutableList().apply {
                    val index = indexOfFirst { it.user == user }
                    removeAt(index)
                    add(index, LocalUser(user, false))
                }.toList()
            )
        }
    }

    private fun onAddUser(user: String) = withScreenModelScope {
        _uiState.update { state ->
            state.copy(
                selectedUsers = state.selectedUsers + user,
                users = state.users.toMutableList().apply {
                    val index = indexOfFirst { it.user == user }
                    removeAt(index)
                    add(index, LocalUser(user, true))
                }.toList()
            )
        }
    }

    private fun onNameChanged(value: String) = withScreenModelScope {
        _uiState.update { state -> state.copy(name = value) }
    }

    private fun onNavigateBack() = withScreenModelScope {
        _navigationActions.send(NavigationAction.Pop)
    }
}