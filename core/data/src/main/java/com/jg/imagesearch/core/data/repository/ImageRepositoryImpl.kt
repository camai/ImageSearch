package com.jg.imagesearch.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.jg.imagesearch.core.data.datasource.ImageLocalDataSource
import com.jg.imagesearch.core.data.datasource.ImageRemoteDataSource
import com.jg.imagesearch.core.data.mediator.ImageRemoteMediator
import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.random.Random

class ImageRepositoryImpl @Inject constructor(
    private val remoteDataSource: ImageRemoteDataSource,
    private val localDataSource: ImageLocalDataSource
) : ImageRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun searchImages(query: String): Flow<PagingData<ImageItem>> {
        val pagingSourceFactory = { localDataSource.pagingSource(query) }

        return Pager(
            config = PagingConfig(
                pageSize = 50,
                initialLoadSize = 50,
                enablePlaceholders = false
            ),
            remoteMediator = ImageRemoteMediator(
                query = query,
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    override fun searchLocalImages(keyword: String): Flow<PagingData<ImageItem>> {
        val pagingSourceFactory = { localDataSource.searchByTitleLocal(keyword) }

        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    override suspend fun getRandomImages(query: String, display: Int): DataResult<List<ImageItem>, String> {
        return runCatching {
            val randomStart = Random.nextInt(1, 100)
            val response = remoteDataSource.searchImages(query = query, display = display, start = randomStart)
            DataResult.Success(response.items.map { it.toDomainModel() }.shuffled())
        }.getOrElse { e ->
            DataResult.Fail(e.message ?: "Unknown API Error")
        }
    }
}
