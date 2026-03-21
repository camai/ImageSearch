package com.jg.imagesearch.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jg.imagesearch.core.data.local.dao.BookmarkDao
import com.jg.imagesearch.core.data.local.dao.SearchImageDao
import com.jg.imagesearch.core.data.local.dao.SearchRemoteKeysDao
import com.jg.imagesearch.core.data.local.entity.BookmarkEntity
import com.jg.imagesearch.core.data.local.entity.SearchImageEntity
import com.jg.imagesearch.core.data.local.entity.SearchRemoteKeysEntity

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
