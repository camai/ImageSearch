package com.jg.imagesearch.core.domain.usecase

import androidx.paging.PagingData
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow

interface SearchLocalImagesUseCase {
    operator fun invoke(keyword: String): Flow<PagingData<ImageItem>>
}
