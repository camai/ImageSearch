package com.jg.imagesearch.core.data.di

import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.RemoveBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.SearchImagesUseCase
import com.jg.imagesearch.core.domain.usecase.ToggleBookmarkUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSearchImagesUseCase(repository: ImageRepository): SearchImagesUseCase {
        return SearchImagesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleBookmarkUseCase(repository: BookmarkRepository): ToggleBookmarkUseCase {
        return ToggleBookmarkUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBookmarksUseCase(repository: BookmarkRepository): GetBookmarksUseCase {
        return GetBookmarksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRemoveBookmarksUseCase(repository: BookmarkRepository): RemoveBookmarksUseCase {
        return RemoveBookmarksUseCase(repository)
    }
}
