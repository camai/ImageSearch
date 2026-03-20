package com.jg.imagesearch.core.model

sealed interface DataResult<out T, out E> {
    data class Success<out T>(val data: T) : DataResult<T, Nothing>
    data class Fail<out E>(val error: E) : DataResult<Nothing, E>
}
