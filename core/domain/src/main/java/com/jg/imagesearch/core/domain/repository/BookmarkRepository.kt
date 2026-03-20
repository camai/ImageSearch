package com.jg.imagesearch.core.domain.repository

import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarks(): Flow<List<ImageItem>>
    suspend fun addBookmark(imageItem: ImageItem)
    suspend fun removeBookmark(imageItem: ImageItem)
    suspend fun removeBookmarks(imageItems: List<ImageItem>)
    suspend fun isBookmarked(link: String): Boolean
}
