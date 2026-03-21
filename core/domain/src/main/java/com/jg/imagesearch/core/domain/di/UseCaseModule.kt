package com.jg.imagesearch.core.domain.di

import com.jg.imagesearch.core.domain.usecase.*
import com.jg.imagesearch.core.domain.usecase.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindSearchImagesUseCase(
        impl: SearchImagesUseCaseImpl
    ): SearchImagesUseCase

    @Binds
    @Singleton
    abstract fun bindToggleBookmarkUseCase(
        impl: ToggleBookmarkUseCaseImpl
    ): ToggleBookmarkUseCase

    @Binds
    @Singleton
    abstract fun bindGetBookmarksUseCase(
        impl: GetBookmarksUseCaseImpl
    ): GetBookmarksUseCase

    @Binds
    @Singleton
    abstract fun bindRemoveBookmarksUseCase(
        impl: RemoveBookmarksUseCaseImpl
    ): RemoveBookmarksUseCase

    @Binds
    @Singleton
    abstract fun bindGetRandomImagesUseCase(
        impl: GetRandomImagesUseCaseImpl
    ): GetRandomImagesUseCase
}
