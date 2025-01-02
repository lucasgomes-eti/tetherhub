package feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

object CreatePostScreen : Screen {
    @Composable
    override fun Content() {
        val createPostScreenModel = getScreenModel<CreatePostScreenModel>()
        val uiState by createPostScreenModel.uiState.collectAsState()
        CreatePost(uiState, createPostScreenModel::onAction)
    }

}