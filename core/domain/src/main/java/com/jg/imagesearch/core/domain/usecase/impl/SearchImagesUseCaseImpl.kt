package com.jg.imagesearch.core.domain.usecase.impl

import androidx.paging.PagingData
import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.domain.usecase.SearchImagesUseCase
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchImagesUseCaseImpl @Inject constructor(
    private val repository: ImageRepository
) : SearchImagesUseCase {
    override operator fun invoke(query: String): Flow<PagingData<ImageItem>> {
        return repository.searchImages(query)
    }
}
