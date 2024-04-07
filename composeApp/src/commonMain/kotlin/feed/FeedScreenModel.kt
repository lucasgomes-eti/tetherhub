package feed

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FeedUiState(
    val posts: List<String>
)

class FeedScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(FeedUiState(listOf("post 1", "post 2")))
    val uiState = _uiState.asStateFlow()

}