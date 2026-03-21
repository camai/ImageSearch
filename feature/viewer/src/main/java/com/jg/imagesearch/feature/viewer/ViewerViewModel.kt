package com.jg.imagesearch.feature.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.GetRandomImagesUseCase
import com.jg.imagesearch.core.domain.usecase.ToggleBookmarkUseCase
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem
import com.jg.imagesearch.core.model.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    private val getRandomImagesUseCase: GetRandomImagesUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _rawImages = MutableStateFlow<List<ImageItem>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiEffect = MutableSharedFlow<UiEffect>()
    val uiEffect: SharedFlow<UiEffect> = _uiEffect.asSharedFlow()

    val images: StateFlow<List<ImageItem>> = _rawImages
        .combine(getBookmarksUseCase()) { images, bookmarks ->
            val bookmarkLinks = bookmarks.map { it.link }.toSet()
            images.map { it.copy(isBookmarked = bookmarkLinks.contains(it.link)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun initialize(selectedItem: ImageItem) {
        if (_rawImages.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _rawImages.value = listOf(selectedItem)

            when (val result = getRandomImagesUseCase("만화", 30)) {
                is DomainResult.Success -> {
                    val filtered = result.data.filter { it.link != selectedItem.link }.take(30)
                    _rawImages.value = listOf(selectedItem) + filtered
                }
                is DomainResult.Fail -> {
                    _uiEffect.emit(UiEffect.ShowSnackbar(result.error))
                }
            }
            _isLoading.value = false
        }
    }

    fun toggleBookmark(item: ImageItem) {
        viewModelScope.launch {
            when (val result = toggleBookmarkUseCase(item)) {
                is DomainResult.Success -> { /* Bookmark state is auto-synced via Flow */ }
                is DomainResult.Fail -> {
                    _uiEffect.emit(UiEffect.ShowSnackbar(result.error))
                }
            }
        }
    }
}
