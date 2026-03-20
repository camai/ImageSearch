package com.jg.imagesearch.core.data.di

import com.jg.imagesearch.core.data.repository.BookmarkRepositoryImpl
import com.jg.imagesearch.core.data.repository.ImageRepositoryImpl
import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.domain.repository.ImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        bookmarkRepositoryImpl: BookmarkRepositoryImpl
    ): BookmarkRepository
}
