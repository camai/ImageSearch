package com.jg.imagesearch.core.model

sealed interface UiEffect {
    data class ShowSnackbar(val message: String) : UiEffect
}
