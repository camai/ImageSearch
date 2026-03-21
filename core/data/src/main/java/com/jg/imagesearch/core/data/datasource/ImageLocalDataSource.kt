package com.jg.imagesearch.core.data.datasource

import androidx.paging.PagingSource
import com.jg.imagesearch.core.database.model.SearchImageEntity
import com.jg.imagesearch.core.database.model.SearchRemoteKeysEntity

interface ImageLocalDataSource {
    fun pagingSource(query: String): PagingSource<Int, SearchImageEntity>
    suspend fun insertAll(images: List<SearchImageEntity>)
    suspend fun clearImages(query: String)
    suspend fun insertRemoteKeys(keys: List<SearchRemoteKeysEntity>)
    suspend fun remoteKeysByLink(link: String): SearchRemoteKeysEntity?
    suspend fun clearRemoteKeys(query: String)
    suspend fun <R> withTransaction(block: suspend () -> R): R
}
