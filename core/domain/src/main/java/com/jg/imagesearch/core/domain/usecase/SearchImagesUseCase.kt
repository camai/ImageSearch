package com.jg.imagesearch.core.domain.usecase

import androidx.paging.PagingData
import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow

class SearchImagesUseCase(
    private val repository: ImageRepository
) {
    operator fun invoke(query: String): Flow<PagingData<ImageItem>> {
        return repository.searchImages(query)
    }
}
