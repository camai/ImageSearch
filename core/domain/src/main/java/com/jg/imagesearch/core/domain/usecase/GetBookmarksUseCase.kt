package com.jg.imagesearch.core.domain.usecase

import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow

class GetBookmarksUseCase(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<ImageItem>> {
        return repository.getBookmarks()
    }
}
