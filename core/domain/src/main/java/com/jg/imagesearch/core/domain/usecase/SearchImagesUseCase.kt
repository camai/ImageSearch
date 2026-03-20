package com.jg.imagesearch.core.domain.usecase

import androidx.paging.PagingData
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow

interface SearchImagesUseCase {
    operator fun invoke(query: String): Flow<PagingData<ImageItem>>
}
