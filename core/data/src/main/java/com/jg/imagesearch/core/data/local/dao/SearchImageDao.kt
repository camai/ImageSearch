package com.jg.imagesearch.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jg.imagesearch.core.data.local.entity.SearchImageEntity

@Dao
interface SearchImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<SearchImageEntity>)

    @Query("SELECT * FROM search_images WHERE searchQuery = :query ORDER BY id ASC")
    fun pagingSource(query: String): PagingSource<Int, SearchImageEntity>

    @Query("DELETE FROM search_images WHERE searchQuery = :query")
    suspend fun clearImages(query: String)
}
