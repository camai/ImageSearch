package com.jg.imagesearch.core.model

data class ImageItem(
    val title: String,
    val link: String,
    val thumbnail: String,
    val sizeHeight: Int,
    val sizeWidth: Int,
    val isBookmarked: Boolean = false
)
