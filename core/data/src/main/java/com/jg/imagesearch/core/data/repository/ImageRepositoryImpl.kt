package com.jg.imagesearch.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jg.imagesearch.core.data.api.ImagePagingSource
import com.jg.imagesearch.core.data.api.NaverImageApi
import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val api: NaverImageApi
) : ImageRepository {
    override fun searchImages(query: String): Flow<PagingData<ImageItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ImagePagingSource(api, query) }
        ).flow
    }
}
