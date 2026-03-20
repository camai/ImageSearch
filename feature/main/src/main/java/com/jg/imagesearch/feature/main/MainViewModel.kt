package com.jg.imagesearch.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.SearchImagesUseCase
import com.jg.imagesearch.core.domain.usecase.ToggleBookmarkUseCase
import com.jg.imagesearch.core.model.ImageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchImagesUseCase: SearchImagesUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    val images: Flow<PagingData<ImageItem>> = searchImagesUseCase("만화")
        .combine(getBookmarksUseCase()) { pagingData, bookmarks ->
            val bookmarkLinks = bookmarks.map { it.link }.toSet()
            pagingData.map { item ->
                item.copy(isBookmarked = bookmarkLinks.contains(item.link))
            }
        }.cachedIn(viewModelScope)

    fun toggleBookmark(item: ImageItem) {
        viewModelScope.launch {
            toggleBookmarkUseCase(item)
        }
    }
}
