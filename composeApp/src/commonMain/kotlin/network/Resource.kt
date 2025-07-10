package network

import response.TetherHubError

sealed interface Resource<out D> {
    data class Success<out D>(val data: D) : Resource<D>
    data class Error(val error: TetherHubError) : Resource<Nothing>
}

fun unexpectedErrorWithException(e: Exception) =
    TetherHubError(
        -1,
        "TH-0",
        "Unexpected error during request. Cause: ${e.message}"
    )

fun unexpectedErrorWithHttpStatusCode(statusCode: Int) =
    TetherHubError(
        statusCode,
        "TH-0",
        "Unexpected http error. Code: $statusCode"
    )

inline fun <T, R> Resource<T>.map(map: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Error -> Resource.Error(error)
        is Resource.Success -> Resource.Success(map(data))
    }
}

fun <T> Resource<T>.asEmptyDataResult(): EmptyResult {
    return map { }
}

inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    return when (this) {
        is Resource.Error -> this
        is Resource.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T> Resource<T>.onError(action: (TetherHubError) -> Unit): Resource<T> {
    return when (this) {
        is Resource.Error -> {
            action(error)
            this
        }

        is Resource.Success -> this
    }
}

typealias EmptyResult = Resource<Unit>