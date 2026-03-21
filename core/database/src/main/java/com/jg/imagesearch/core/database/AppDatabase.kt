package com.jg.imagesearch.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jg.imagesearch.core.database.dao.BookmarkDao
import com.jg.imagesearch.core.database.dao.SearchImageDao
import com.jg.imagesearch.core.database.dao.SearchRemoteKeysDao
import com.jg.imagesearch.core.database.model.BookmarkEntity
import com.jg.imagesearch.core.database.model.SearchImageEntity
import com.jg.imagesearch.core.database.model.SearchRemoteKeysEntity

@Database(
    entities = [BookmarkEntity::class, SearchImageEntity::class, SearchRemoteKeysEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun searchImageDao(): SearchImageDao
    abstract fun searchRemoteKeysDao(): SearchRemoteKeysDao
}
