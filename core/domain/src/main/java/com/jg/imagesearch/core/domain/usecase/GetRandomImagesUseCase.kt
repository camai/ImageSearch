package com.jg.imagesearch.core.domain.usecase

import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem

interface GetRandomImagesUseCase {
    suspend operator fun invoke(query: String, display: Int): DomainResult<List<ImageItem>, String>
}
