package com.jg.imagesearch.core.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.jg.imagesearch.core.data.datasource.ImageLocalDataSource
import com.jg.imagesearch.core.data.datasource.ImageRemoteDataSource
import com.jg.imagesearch.core.database.model.SearchImageEntity
import com.jg.imagesearch.core.database.model.SearchRemoteKeysEntity

@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val query: String,
    private val remoteDataSource: ImageRemoteDataSource,
    private val localDataSource: ImageLocalDataSource
) : RemoteMediator<Int, SearchImageEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchImageEntity>
    ): MediatorResult {
        return try {
            val start = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(state.config.pageSize) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val response = remoteDataSource.searchImages(
                query = query,
                display = state.config.pageSize,
                start = start
            )
            val items = response.items
            val endOfPaginationReached = items.isEmpty()

            localDataSource.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    localDataSource.clearRemoteKeys(query)
                    localDataSource.clearImages(query)
                }
                val prevKey = if (start == 1) null else start - state.config.pageSize
                val nextKey = if (endOfPaginationReached) null else start + state.config.pageSize

                val keys = items.map {
                    SearchRemoteKeysEntity(
                        link = it.link,
                        prevKey = prevKey,
                        nextKey = nextKey,
                        searchQuery = query
                    )
                }
                localDataSource.insertRemoteKeys(keys)

                val imageEntities = items.map {
                    SearchImageEntity(
                        link = it.link,
                        title = it.title,
                        thumbnail = it.thumbnail,
                        sizeHeight = it.sizeheight.toIntOrNull() ?: 0,
                        sizeWidth = it.sizewidth.toIntOrNull() ?: 0,
                        searchQuery = query
                    )
                }
                localDataSource.insertAll(imageEntities)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SearchImageEntity>): SearchRemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo -> localDataSource.remoteKeysByLink(repo.link) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, SearchImageEntity>): SearchRemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo -> localDataSource.remoteKeysByLink(repo.link) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, SearchImageEntity>): SearchRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.link?.let { link ->
                localDataSource.remoteKeysByLink(link)
            }
        }
    }
}
