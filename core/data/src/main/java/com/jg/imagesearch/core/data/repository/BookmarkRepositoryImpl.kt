package com.jg.imagesearch.core.data.repository

import com.jg.imagesearch.core.data.datasource.BookmarkLocalDataSource
import com.jg.imagesearch.core.database.model.toDomainModel
import com.jg.imagesearch.core.database.model.toEntity
import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkLocalDataSource: BookmarkLocalDataSource
) : BookmarkRepository {
    override fun getBookmarks(): Flow<List<ImageItem>> {
        return bookmarkLocalDataSource.getBookmarks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addBookmark(imageItem: ImageItem): DataResult<Boolean, String> {
        return runCatching {
            bookmarkLocalDataSource.insertBookmark(imageItem.toEntity())
            DataResult.Success(true)
        }.getOrElse { e ->
            DataResult.Fail(e.message ?: "Unknown Error")
        }
    }

    override suspend fun removeBookmark(imageItem: ImageItem): DataResult<Boolean, String> {
        return runCatching {
            bookmarkLocalDataSource.deleteBookmark(imageItem.toEntity())
            DataResult.Success(true)
        }.getOrElse { e ->
            DataResult.Fail(e.message ?: "Unknown Error")
        }
    }

    override suspend fun removeBookmarks(imageItems: List<ImageItem>): DataResult<Boolean, String> {
        return runCatching {
            bookmarkLocalDataSource.deleteBookmarks(imageItems.map { it.toEntity() })
            DataResult.Success(true)
        }.getOrElse { e ->
            DataResult.Fail(e.message ?: "Unknown Error")
        }
    }

    override suspend fun isBookmarked(link: String): Boolean {
        return bookmarkLocalDataSource.isBookmarked(link)
    }
}
