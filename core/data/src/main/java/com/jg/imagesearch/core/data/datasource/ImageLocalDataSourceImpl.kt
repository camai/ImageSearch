package com.jg.imagesearch.core.data.datasource

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.jg.imagesearch.core.database.AppDatabase
import com.jg.imagesearch.core.database.model.SearchImageEntity
import com.jg.imagesearch.core.database.model.SearchRemoteKeysEntity
import javax.inject.Inject

class ImageLocalDataSourceImpl @Inject constructor(
    private val db: AppDatabase
) : ImageLocalDataSource {

    private val searchImageDao = db.searchImageDao()
    private val remoteKeysDao = db.searchRemoteKeysDao()

    override fun pagingSource(query: String): PagingSource<Int, SearchImageEntity> {
        return searchImageDao.pagingSource(query)
    }

    override suspend fun insertAll(images: List<SearchImageEntity>) {
        searchImageDao.insertAll(images)
    }

    override suspend fun clearImages(query: String) {
        searchImageDao.clearImages(query)
    }

    override suspend fun insertRemoteKeys(keys: List<SearchRemoteKeysEntity>) {
        remoteKeysDao.insertAll(keys)
    }

    override suspend fun remoteKeysByLink(link: String): SearchRemoteKeysEntity? {
        return remoteKeysDao.remoteKeysByLink(link)
    }

    override suspend fun clearRemoteKeys(query: String) {
        remoteKeysDao.clearRemoteKeys(query)
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return db.withTransaction(block)
    }
}
