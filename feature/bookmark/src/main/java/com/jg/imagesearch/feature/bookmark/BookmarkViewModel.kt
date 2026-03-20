package com.jg.imagesearch.feature.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.RemoveBookmarksUseCase
import com.jg.imagesearch.core.model.ImageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val removeBookmarksUseCase: RemoveBookmarksUseCase
) : ViewModel() {

    val bookmarks: StateFlow<List<ImageItem>> = getBookmarksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeBookmarks(items: List<ImageItem>) {
        viewModelScope.launch {
            removeBookmarksUseCase(items)
        }
    }
}
