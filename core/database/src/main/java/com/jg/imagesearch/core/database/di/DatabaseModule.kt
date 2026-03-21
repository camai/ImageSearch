package com.jg.imagesearch.core.database.di

import android.content.Context
import androidx.room.Room
import com.jg.imagesearch.core.database.AppDatabase
import com.jg.imagesearch.core.database.dao.BookmarkDao
import com.jg.imagesearch.core.database.dao.SearchImageDao
import com.jg.imagesearch.core.database.dao.SearchRemoteKeysDao
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
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(appDatabase: AppDatabase): BookmarkDao {
        return appDatabase.bookmarkDao()
    }

    @Provides
    @Singleton
    fun provideSearchImageDao(appDatabase: AppDatabase): SearchImageDao {
        return appDatabase.searchImageDao()
    }

    @Provides
    @Singleton
    fun provideSearchRemoteKeysDao(appDatabase: AppDatabase): SearchRemoteKeysDao {
        return appDatabase.searchRemoteKeysDao()
    }
}
