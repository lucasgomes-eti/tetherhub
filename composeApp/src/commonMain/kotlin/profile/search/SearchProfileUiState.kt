package profile.search

import response.PageResponse
import response.PublicProfileResponse

data class SearchProfileUiState(
    val searchQuery: String,
    val isLoading: Boolean,
    val errorMessage: String,
    val profiles: PageResponse<PublicProfileResponse>
)
