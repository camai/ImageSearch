package com.jg.imagesearch.core.model

sealed interface UiEffect {
    data class ShowSnackbar(
        val message: SnackbarMessage,
        val args: List<String> = emptyList(),
        val type: SnackbarType = SnackbarType.INFO
    ) : UiEffect
}

enum class SnackbarType { INFO, SUCCESS, ERROR }

enum class SnackbarMessage {
    BOOKMARK_ADDED,
    BOOKMARK_REMOVED,
    BOOKMARKS_ADDED,
    BOOKMARKS_DELETED,
    ERROR_RELATED_IMAGES,
    ERROR_DEFAULT
}
