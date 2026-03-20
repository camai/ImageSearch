package com.jg.imagesearch.core.data.repository

import com.jg.imagesearch.core.data.local.dao.BookmarkDao
import com.jg.imagesearch.core.data.local.entity.toDomainModel
import com.jg.imagesearch.core.data.local.entity.toEntity
import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {
    override fun getBookmarks(): Flow<List<ImageItem>> {
        return bookmarkDao.getBookmarks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addBookmark(imageItem: ImageItem): DataResult<Boolean, String> {
        return try {
            bookmarkDao.insertBookmark(imageItem.toEntity())
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Fail(e.message ?: "Unknown Error")
        }
    }

    override suspend fun removeBookmark(imageItem: ImageItem): DataResult<Boolean, String> {
        return try {
            bookmarkDao.deleteBookmark(imageItem.toEntity())
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Fail(e.message ?: "Unknown Error")
        }
    }

    override suspend fun removeBookmarks(imageItems: List<ImageItem>): DataResult<Boolean, String> {
        return try {
            bookmarkDao.deleteBookmarks(imageItems.map { it.toEntity() })
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Fail(e.message ?: "Unknown Error")
        }
    }

    override suspend fun isBookmarked(link: String): Boolean {
        return bookmarkDao.isBookmarked(link)
    }
}
