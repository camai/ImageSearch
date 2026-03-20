package com.jg.imagesearch.core.data.api.dto

import com.jg.imagesearch.core.model.ImageItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageSearchResponse(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<ImageItemDto>
)

@Serializable
data class ImageItemDto(
    val title: String,
    val link: String,
    val thumbnail: String,
    val sizeheight: String,
    val sizewidth: String
) {
    fun toDomainModel(): ImageItem {
        return ImageItem(
            title = title,
            link = link,
            thumbnail = thumbnail,
            sizeHeight = sizeheight.toIntOrNull() ?: 0,
            sizeWidth = sizewidth.toIntOrNull() ?: 0,
            isBookmarked = false
        )
    }
}
