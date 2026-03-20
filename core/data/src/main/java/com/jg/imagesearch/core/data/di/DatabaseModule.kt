package com.jg.imagesearch.core.data.di

import android.content.Context
import androidx.room.Room
import com.jg.imagesearch.core.data.local.AppDatabase
import com.jg.imagesearch.core.data.local.dao.BookmarkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "imagesearch_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(appDatabase: AppDatabase): BookmarkDao {
        return appDatabase.bookmarkDao()
    }
}
