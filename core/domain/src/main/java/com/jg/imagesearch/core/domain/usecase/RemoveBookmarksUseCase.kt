package com.jg.imagesearch.core.domain.usecase

import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem

interface RemoveBookmarksUseCase {
    suspend operator fun invoke(imageItems: List<ImageItem>): DomainResult<Boolean, String>
}
