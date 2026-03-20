package com.jg.imagesearch.core.domain.usecase

import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.model.ImageItem

class ToggleBookmarkUseCase(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(imageItem: ImageItem) {
        if (repository.isBookmarked(imageItem.link)) {
            repository.removeBookmark(imageItem)
        } else {
            repository.addBookmark(imageItem.copy(isBookmarked = true))
        }
    }
}
