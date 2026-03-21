package com.jg.imagesearch.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jg.imagesearch.core.database.model.SearchRemoteKeysEntity

@Dao
interface SearchRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<SearchRemoteKeysEntity>)

    @Query("SELECT * FROM search_remote_keys WHERE link = :link")
    suspend fun remoteKeysByLink(link: String): SearchRemoteKeysEntity?

    @Query("DELETE FROM search_remote_keys WHERE searchQuery = :query")
    suspend fun clearRemoteKeys(query: String)
}
