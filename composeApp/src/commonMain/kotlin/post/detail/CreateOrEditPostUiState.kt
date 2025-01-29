package post.detail

data class CreateOrEditPostUiState(
    val publication: String,
    val isLoading: Boolean,
    val publicationFieldError: String,
    val errorMsg: String,
    val topAppBarTitle: String,
)
