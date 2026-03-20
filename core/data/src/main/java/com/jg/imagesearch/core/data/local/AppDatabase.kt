package com.jg.imagesearch.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jg.imagesearch.core.data.local.dao.BookmarkDao
import com.jg.imagesearch.core.data.local.entity.BookmarkEntity

@Database(
    entities = [BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
