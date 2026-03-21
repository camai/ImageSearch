package com.jg.imagesearch.core.data.datasource

import com.jg.imagesearch.core.database.model.BookmarkEntity
import kotlinx.coroutines.flow.Flow

interface BookmarkLocalDataSource {
    fun getBookmarks(): Flow<List<BookmarkEntity>>
    suspend fun insertBookmark(entity: BookmarkEntity)
    suspend fun deleteBookmark(entity: BookmarkEntity)
    suspend fun deleteBookmarks(entities: List<BookmarkEntity>)
    suspend fun isBookmarked(link: String): Boolean
}
