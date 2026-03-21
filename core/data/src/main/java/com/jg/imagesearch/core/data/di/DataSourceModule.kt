package com.jg.imagesearch.core.data.di

import com.jg.imagesearch.core.data.datasource.BookmarkLocalDataSource
import com.jg.imagesearch.core.data.datasource.BookmarkLocalDataSourceImpl
import com.jg.imagesearch.core.data.datasource.ImageLocalDataSource
import com.jg.imagesearch.core.data.datasource.ImageLocalDataSourceImpl
import com.jg.imagesearch.core.data.datasource.ImageRemoteDataSource
import com.jg.imagesearch.core.data.datasource.ImageRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindImageRemoteDataSource(
        impl: ImageRemoteDataSourceImpl
    ): ImageRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindImageLocalDataSource(
        impl: ImageLocalDataSourceImpl
    ): ImageLocalDataSource

    @Binds
    @Singleton
    abstract fun bindBookmarkLocalDataSource(
        impl: BookmarkLocalDataSourceImpl
    ): BookmarkLocalDataSource
}
