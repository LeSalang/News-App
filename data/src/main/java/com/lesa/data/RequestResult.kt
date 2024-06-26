package com.lesa.data

sealed class RequestResult<out E : Any>(open val data: E? = null) {
    class Error<E : Any>(data: E? = null, val error: Throwable? = null) : RequestResult<E>(data)
    class InProgress<E : Any>(data: E? = null) : RequestResult<E>(data)
    class Success<E : Any>(override val data: E) : RequestResult<E>(data)
}

fun <I : Any, O : Any> RequestResult<I>.map(mapper: (I) -> O): RequestResult<O> {
    return when (this) {
        is RequestResult.Error -> RequestResult.Error(data?.let(mapper))
        is RequestResult.InProgress -> RequestResult.InProgress(data?.let(mapper))
        is RequestResult.Success -> {
            val outData: O = mapper(data)
            RequestResult.Success(outData)
        }
    }
}

internal fun <T : Any> Result<T>.toRequestResult(): RequestResult<T> {
    return when {
        isSuccess -> RequestResult.Success(getOrThrow())
        isFailure -> RequestResult.Error()
        else -> error("Result must be either success or failure")
    }
}
