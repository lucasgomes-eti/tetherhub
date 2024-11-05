package feed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import profile.User

class FeedScreenModel : ScreenModel {

    private val _uiState = MutableStateFlow(
        FeedUiState(
            listOf(
                LocalPost(
                    Post(
                        "1",
                        User("1", "scary"),
                        "We are currently aware of an issue that these balance changes are only reflected in a game mode that appears in the arcade. So we’re gonna call it “Balanced Overwatch” for now. Sorry for any confusion.",
                        3
                    ), true
                ),
                LocalPost(
                    Post(
                        "2",
                        User("1", "scary"),
                        "There are also some new challenges that are granting some of our developer’s doodles as sprays. We’re not sure why that is happening, but they are really cool looking.",
                        0
                    ), false
                )
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(feedAction: FeedAction) {
        when (feedAction) {
            is FeedAction.Like -> onPostLiked(feedAction.postId)
        }
    }

    private fun onPostLiked(postId: String) {
        screenModelScope.launch {
            _uiState.update { state ->
                state.copy(posts = _uiState.value.posts.map {
                    val isLiked = if (it.id == postId) !it.isLiked else it.isLiked
                    it.copy(
                        post = Post(
                            id = it.id,
                            author = it.author,
                            content = it.content,
                            likes = if (it.id == postId) if (isLiked) it.likes + 1 else it.likes - 1 else it.likes
                        ),
                        isLiked = isLiked
                    )
                })
            }
        }
    }

}