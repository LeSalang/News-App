package com.lesa.news_data

interface MergeStrategy<E> {

    fun merge(right: E, left: E): E
}

internal class DefaultMergeStrategy<T : Any> : MergeStrategy<RequestResult<T>> {

    override fun merge(
        cache: RequestResult<T>,
        server: RequestResult<T>,
    ): RequestResult<T> {
        return when {
            cache is RequestResult.InProgress && server is RequestResult.InProgress -> return merge(cache, server)
            cache is RequestResult.Success && server is RequestResult.InProgress -> return merge(cache, server)
            cache is RequestResult.InProgress && server is RequestResult.Success -> return merge(cache, server)
            cache is RequestResult.Success && server is RequestResult.Error -> return merge(cache, server)
            else -> error("Unimplemented brunch")
        }
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

    private fun merge(
        cache: RequestResult.Success<T>,
        server: RequestResult.InProgress<T>
    ): RequestResult<T> {
        return RequestResult.InProgress(cache.data)
    }

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
        return RequestResult.Error(data =cache.data, error = server.error)
    }
}