package feed

data class CreatePostUiState(
    val publication: String,
    val isLoading: Boolean,
    val publicationFieldError: String,
    val errorMsg: String,
    val event: CreatePostEvent
)
