package com.lesa.data

interface MergeStrategy<E> {

    fun merge(right: E, left: E): E
}

internal class DefaultMergeStrategy<T : Any> : MergeStrategy<RequestResult<T>> {

    @Suppress("CyclomaticComplexMethod", "ReturnCount")
    override fun merge(
        right: RequestResult<T>,
        left: RequestResult<T>,
    ): RequestResult<T> {
        return when {
            right is RequestResult.Error && left is RequestResult.Error -> return merge(right, left)
            right is RequestResult.Error && left is RequestResult.InProgress -> return merge(right, left)
            right is RequestResult.Error && left is RequestResult.Success -> return merge(right, left)
            right is RequestResult.InProgress && left is RequestResult.Error -> return merge(right, left)
            right is RequestResult.InProgress && left is RequestResult.InProgress -> return merge(right, left)
            right is RequestResult.InProgress && left is RequestResult.Success -> return merge(right, left)
            right is RequestResult.Success && left is RequestResult.Error -> return merge(right, left)
            right is RequestResult.Success && left is RequestResult.InProgress -> return merge(right, left)
            right is RequestResult.Success && left is RequestResult.Success -> return merge(right, left)
            else -> error("Unimplemented brunch")
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: RequestResult.Error<T>,
        server: RequestResult.Error<T>
    ): RequestResult<T> {
        return RequestResult.Error(data = null, error = server.error)
    }

    private fun merge(
        cache: RequestResult.Error<T>,
        server: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return RequestResult.Error(data = cache.data ?: server.data, error = cache.error)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: RequestResult.Error<T>,
        server: RequestResult.Success<T>
    ): RequestResult<T> {
        return RequestResult.Success(data = server.data)
    }

    private fun merge(
        cache: RequestResult.InProgress<T>,
        server: RequestResult.Error<T>
    ): RequestResult<T> {
        return RequestResult.Error(data = server.data ?: cache.data, error = server.error)
    }

    private fun merge(
        cache: RequestResult.InProgress<T>,
        server: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return if (server.data != null) {
            RequestResult.InProgress(server.data)
        } else {
            RequestResult.InProgress(cache.data)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: RequestResult.InProgress<T>,
        server: RequestResult.Success<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(server.data)
    }

    private fun merge(
        cache: RequestResult.Success<T>,
        server: RequestResult.Error<T>
    ): RequestResult<T> {
        return RequestResult.Error(data = cache.data, error = server.error)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: RequestResult.Success<T>,
        server: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(cache.data)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: RequestResult.Success<T>,
        server: RequestResult.Success<T>
    ): RequestResult<T> {
        return RequestResult.Success(data = server.data)
    }
}
