package com.jg.imagesearch.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jg.imagesearch.core.data.api.ImagePagingSource
import com.jg.imagesearch.core.data.api.NaverImageApi
import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.random.Random

class ImageRepositoryImpl @Inject constructor(
    private val api: NaverImageApi
) : ImageRepository {
    override fun searchImages(query: String): Flow<PagingData<ImageItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                initialLoadSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ImagePagingSource(api, query) }
        ).flow
    }

    override suspend fun getRandomImages(query: String, display: Int): DataResult<List<ImageItem>, String> {
        return runCatching {
            val randomStart = Random.nextInt(1, 100)
            val response = api.searchImages(query = query, display = display, start = randomStart)
            DataResult.Success(response.items.map { it.toDomainModel() }.shuffled())
        }.getOrElse { e ->
            DataResult.Fail(e.message ?: "Unknown API Error")
        }
    }
}
