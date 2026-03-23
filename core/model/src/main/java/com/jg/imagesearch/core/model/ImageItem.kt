package com.jg.imagesearch.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class ImageItem(
    val title: String,
    val link: String,
    val thumbnail: String,
    val sizeHeight: Int,
    val sizeWidth: Int,
    val isBookmarked: Boolean = false
)
