package com.productivitystreak.data.repository

sealed class RepositoryResult<out T> {
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class NetworkError(val throwable: Throwable? = null) : RepositoryResult<Nothing>()
    data class DbError(val throwable: Throwable) : RepositoryResult<Nothing>()
    data class PermissionError(val throwable: Throwable? = null) : RepositoryResult<Nothing>()
    data class UnknownError(val throwable: Throwable? = null) : RepositoryResult<Nothing>()
}

inline fun <T> RepositoryResult<T>.onSuccess(action: (T) -> Unit): RepositoryResult<T> {
    if (this is RepositoryResult.Success) action(data)
    return this
}

inline fun <T> RepositoryResult<T>.onFailure(action: (RepositoryResult<T>) -> Unit): RepositoryResult<T> {
    if (this !is RepositoryResult.Success) action(this)
    return this
}
