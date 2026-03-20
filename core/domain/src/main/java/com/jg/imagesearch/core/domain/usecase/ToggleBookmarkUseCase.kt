package com.jg.imagesearch.core.domain.usecase

import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem

interface ToggleBookmarkUseCase {
    suspend operator fun invoke(imageItem: ImageItem): DomainResult<Boolean, String>
}
