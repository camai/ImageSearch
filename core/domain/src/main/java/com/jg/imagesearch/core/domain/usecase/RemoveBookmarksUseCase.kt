package com.jg.imagesearch.core.domain.usecase

import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.model.ImageItem

class RemoveBookmarksUseCase(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(imageItems: List<ImageItem>) {
        repository.removeBookmarks(imageItems)
    }
}
