package com.jg.imagesearch.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jg.imagesearch.core.database.model.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmarks(bookmarks: List<BookmarkEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE link = :link)")
    suspend fun isBookmarked(link: String): Boolean
}
