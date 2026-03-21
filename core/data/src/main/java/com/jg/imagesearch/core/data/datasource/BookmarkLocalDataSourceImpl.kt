package com.jg.imagesearch.core.data.datasource

import com.jg.imagesearch.core.database.dao.BookmarkDao
import com.jg.imagesearch.core.database.model.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkLocalDataSourceImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkLocalDataSource {

    override fun getBookmarks(): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getBookmarks()
    }

    override suspend fun insertBookmark(entity: BookmarkEntity) {
        bookmarkDao.insertBookmark(entity)
    }

    override suspend fun deleteBookmark(entity: BookmarkEntity) {
        bookmarkDao.deleteBookmark(entity)
    }

    override suspend fun deleteBookmarks(entities: List<BookmarkEntity>) {
        bookmarkDao.deleteBookmarks(entities)
    }

    override suspend fun isBookmarked(link: String): Boolean {
        return bookmarkDao.isBookmarked(link)
    }
}
