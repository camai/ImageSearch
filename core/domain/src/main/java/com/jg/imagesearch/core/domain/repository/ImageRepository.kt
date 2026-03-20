package com.jg.imagesearch.core.domain.repository

import androidx.paging.PagingData
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun searchImages(query: String): Flow<PagingData<ImageItem>>
}
