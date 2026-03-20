package com.jg.imagesearch.core.domain.usecase.impl

import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.domain.usecase.RemoveBookmarksUseCase
import com.jg.imagesearch.core.model.ImageItem
import javax.inject.Inject

class RemoveBookmarksUseCaseImpl @Inject constructor(
    private val repository: BookmarkRepository
) : RemoveBookmarksUseCase {
    override suspend operator fun invoke(imageItems: List<ImageItem>): DomainResult<Boolean, String> {
        val result = repository.removeBookmarks(imageItems)
        return when (result) {
            is DataResult.Success -> DomainResult.Success(data = true)
            is DataResult.Fail -> DomainResult.Fail(error = result.error)
        }
    }
}
