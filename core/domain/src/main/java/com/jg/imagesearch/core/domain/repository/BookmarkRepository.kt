package com.jg.imagesearch.core.domain.repository

import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarks(): Flow<List<ImageItem>>
    suspend fun addBookmark(imageItem: ImageItem): DataResult<Boolean, String>
    suspend fun removeBookmark(imageItem: ImageItem): DataResult<Boolean, String>
    suspend fun removeBookmarks(imageItems: List<ImageItem>): DataResult<Boolean, String>
    suspend fun isBookmarked(link: String): Boolean
}
