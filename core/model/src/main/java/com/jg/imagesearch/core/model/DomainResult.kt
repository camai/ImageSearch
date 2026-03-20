package com.jg.imagesearch.core.model

sealed interface DomainResult<out T, out E> {
    data class Success<out T>(val data: T) : DomainResult<T, Nothing>
    data class Fail<out E>(val error: E) : DomainResult<Nothing, E>
}
