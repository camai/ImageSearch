package com.jg.imagesearch.core.domain.usecase.impl

import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarksUseCaseImpl @Inject constructor(
    private val repository: BookmarkRepository
) : GetBookmarksUseCase {
    override operator fun invoke(): Flow<List<ImageItem>> {
        return repository.getBookmarks()
    }
}
