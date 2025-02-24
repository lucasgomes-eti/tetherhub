package response

import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    val items: List<T>,
    val totalPages: Int,
    val totalItems: Long,
    val currentPage: Int,
    val lastPage: Boolean
)