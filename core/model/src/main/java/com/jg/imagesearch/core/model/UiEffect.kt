package com.jg.imagesearch.core.model

sealed interface UiEffect {
    data class ShowSnackbar(
        val message: String,
        val type: SnackbarType = SnackbarType.INFO
    ) : UiEffect
}

enum class SnackbarType { INFO, SUCCESS, ERROR }
