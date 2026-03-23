package com.jg.imagesearch.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.SearchImagesUseCase
import com.jg.imagesearch.core.domain.usecase.ToggleBookmarkUseCase
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem
import com.jg.imagesearch.core.model.SnackbarMessage
import com.jg.imagesearch.core.model.SnackbarType
import com.jg.imagesearch.core.model.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchImagesUseCase: SearchImagesUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    val searchResults: Flow<PagingData<ImageItem>> = searchImagesUseCase("만화")
        .combine(getBookmarksUseCase()) { pagingData, bookmarks ->
            val bookmarkLinks = bookmarks.map { it.link }.toSet()
            pagingData.map { item ->
                item.copy(isBookmarked = bookmarkLinks.contains(item.link))
            }
        }
        .cachedIn(viewModelScope)

    // _uiEffect is moved down here to keep variables together
    private val _uiEffect = MutableSharedFlow<UiEffect>()
    val uiEffect: SharedFlow<UiEffect> = _uiEffect.asSharedFlow()

    fun toggleBookmark(item: ImageItem) {
        viewModelScope.launch {
            when (val result = toggleBookmarkUseCase(item)) {
                is DomainResult.Success -> {
                    val messageEnum = if (item.isBookmarked) SnackbarMessage.BOOKMARK_REMOVED else SnackbarMessage.BOOKMARK_ADDED
                    _uiEffect.emit(UiEffect.ShowSnackbar(messageEnum, type = SnackbarType.SUCCESS))
                }
                is DomainResult.Fail -> {
                    _uiEffect.emit(UiEffect.ShowSnackbar(SnackbarMessage.ERROR_DEFAULT, listOf(result.error), SnackbarType.ERROR))
                }
            }
        }
    }

    fun bookmarkAll(items: List<ImageItem>) {
        viewModelScope.launch {
            val targets = items.filter { !it.isBookmarked }
            var successCount = 0
            targets.forEach { item ->
                when (toggleBookmarkUseCase(item)) {
                    is DomainResult.Success -> successCount++
                    is DomainResult.Fail -> { /* 개별 실패 무시, 성공 건수만 추적 */ }
                }
            }
            if (successCount > 0) {
                _uiEffect.emit(
                    UiEffect.ShowSnackbar(SnackbarMessage.BOOKMARKS_ADDED, listOf("$successCount"), SnackbarType.SUCCESS)
                )
            }
        }
    }
}
