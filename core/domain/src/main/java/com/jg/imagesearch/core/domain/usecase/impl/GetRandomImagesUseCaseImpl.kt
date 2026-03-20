package com.jg.imagesearch.core.domain.usecase.impl

import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.domain.usecase.GetRandomImagesUseCase
import com.jg.imagesearch.core.model.ImageItem
import javax.inject.Inject

class GetRandomImagesUseCaseImpl @Inject constructor(
    private val repository: ImageRepository
) : GetRandomImagesUseCase {
    override suspend operator fun invoke(query: String, display: Int): DomainResult<List<ImageItem>, String> {
        return when (val result = repository.getRandomImages(query, display)) {
            is DataResult.Success -> DomainResult.Success(result.data)
            is DataResult.Fail -> DomainResult.Fail(result.error)
        }
    }
}
