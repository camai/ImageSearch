package com.jg.imagesearch.feature.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.GetRandomImagesUseCase
import com.jg.imagesearch.core.domain.usecase.ToggleBookmarkUseCase
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem
import com.jg.imagesearch.core.model.SnackbarType
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

            // 선택한 이미지의 제목으로 관련 이미지 검색 (하드코딩 제거)
            val relatedQuery = selectedItem.title
                .replace(Regex("<[^>]*>"), "")  // HTML 태그 제거
                .trim()
                .take(20)
                .ifBlank { "이미지" }

            when (val result = getRandomImagesUseCase(relatedQuery, 30)) {
                is DomainResult.Success -> {
                    val filtered = result.data.filter { it.link != selectedItem.link }.take(30)
                    _rawImages.value = listOf(selectedItem) + filtered
                }
                is DomainResult.Fail -> {
                    _uiEffect.emit(
                        UiEffect.ShowSnackbar("관련 이미지를 불러오지 못했습니다: ${result.error}", SnackbarType.ERROR)
                    )
                }
            }
            _isLoading.value = false
        }
    }

    fun toggleBookmark(item: ImageItem) {
        viewModelScope.launch {
            when (val result = toggleBookmarkUseCase(item)) {
                is DomainResult.Success -> {
                    val message = if (item.isBookmarked) "북마크를 해제했습니다" else "북마크에 추가했습니다"
                    _uiEffect.emit(UiEffect.ShowSnackbar(message, SnackbarType.SUCCESS))
                }
                is DomainResult.Fail -> {
                    _uiEffect.emit(UiEffect.ShowSnackbar(result.error, SnackbarType.ERROR))
                }
            }
        }
    }
}
