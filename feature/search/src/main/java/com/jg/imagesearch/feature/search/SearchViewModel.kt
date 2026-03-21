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
import com.jg.imagesearch.core.model.SnackbarType
import com.jg.imagesearch.core.model.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchImagesUseCase: SearchImagesUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiEffect = MutableSharedFlow<UiEffect>()
    val uiEffect: SharedFlow<UiEffect> = _uiEffect.asSharedFlow()

    val searchResults: Flow<PagingData<ImageItem>> = _query
        .debounce(1000L)
        .filter { it.isNotBlank() }
        .flatMapLatest { q ->
            searchImagesUseCase(q)
                .cachedIn(viewModelScope)
                .combine(getBookmarksUseCase()) { pagingData, bookmarks ->
                    val bookmarkLinks = bookmarks.map { it.link }.toSet()
                    pagingData.map { item ->
                        item.copy(isBookmarked = bookmarkLinks.contains(item.link))
                    }
                }
        }
        .cachedIn(viewModelScope)

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun toggleBookmark(item: ImageItem) {
        viewModelScope.launch {
            when (val result = toggleBookmarkUseCase(item)) {
                is DomainResult.Success -> {
                    val messageRes = if (item.isBookmarked) "북마크를 해제했습니다" else "북마크에 추가했습니다"
                    _uiEffect.emit(UiEffect.ShowSnackbar(messageRes, SnackbarType.SUCCESS))
                }
                is DomainResult.Fail -> {
                    _uiEffect.emit(UiEffect.ShowSnackbar(result.error, SnackbarType.ERROR))
                }
            }
        }
    }
}
