package profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import util.Result

class ProfileScreenModel(private val profileClient: ProfileClient) : ScreenModel {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            username = "-",
            email = "-",
            isLoading = false,
            errorMsg = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfile()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.DismissError -> onDismissError()
        }
    }

    private fun onDismissError() {
        _uiState.update { state -> state.copy(errorMsg = "") }
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            when (val response = profileClient.getProfile()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            username = response.data.username,
                            email = response.data.email,
                            isLoading = false
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            username = "-",
                            email = "-",
                            isLoading = false,
                            errorMsg = "${response.error.internalCode} - ${response.error.message}"
                        )
                    }
                }
            }
        }
    }
}