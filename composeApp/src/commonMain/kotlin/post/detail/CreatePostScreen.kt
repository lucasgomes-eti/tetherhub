package post.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import navigation.ObserveNavigationEvents

object CreatePostScreen : Screen {

    @Composable
    override fun Content() {
        val createOrEditPostScreenModel = koinScreenModel<CreateOrEditPostScreenModel>()
        val uiState by createOrEditPostScreenModel.uiState.collectAsState()
        ObserveNavigationEvents(createOrEditPostScreenModel.navigationActions)
        CreateOrEditPost(uiState, createOrEditPostScreenModel::onAction)
    }
}