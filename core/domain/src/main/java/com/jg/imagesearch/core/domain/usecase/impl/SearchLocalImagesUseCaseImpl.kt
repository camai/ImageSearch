package com.jg.imagesearch.core.domain.usecase.impl

import androidx.paging.PagingData
import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.domain.usecase.SearchLocalImagesUseCase
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchLocalImagesUseCaseImpl @Inject constructor(
    private val repository: ImageRepository
) : SearchLocalImagesUseCase {
    override operator fun invoke(keyword: String): Flow<PagingData<ImageItem>> {
        return repository.searchLocalImages(keyword)
    }
}
