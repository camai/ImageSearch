package com.jg.imagesearch.core.domain.usecase.impl

import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.domain.usecase.ToggleBookmarkUseCase
import com.jg.imagesearch.core.model.ImageItem
import javax.inject.Inject

class ToggleBookmarkUseCaseImpl @Inject constructor(
    private val repository: BookmarkRepository
) : ToggleBookmarkUseCase {
    override suspend operator fun invoke(imageItem: ImageItem): DomainResult<Boolean, String> {
        val result = if (repository.isBookmarked(imageItem.link)) {
            repository.removeBookmark(imageItem)
        } else {
            repository.addBookmark(imageItem.copy(isBookmarked = true))
        }
        return when (result) {
            is DataResult.Success -> DomainResult.Success(data = true)
            is DataResult.Fail -> DomainResult.Fail(error = result.error)
        }
    }
}
