package profile

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState("-", "-"))
    val uiState = _uiState.asStateFlow()
}