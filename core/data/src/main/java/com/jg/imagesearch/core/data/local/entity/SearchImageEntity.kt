package com.jg.imagesearch.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jg.imagesearch.core.model.ImageItem

@Entity(tableName = "search_images")
data class SearchImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val link: String,
    val title: String,
    val thumbnail: String,
    val sizeHeight: Int,
    val sizeWidth: Int,
    val searchQuery: String
) {
    fun toDomainModel(): ImageItem {
        return ImageItem(
            title = title,
            link = link,
            thumbnail = thumbnail,
            sizeHeight = sizeHeight,
            sizeWidth = sizeWidth,
            isBookmarked = false
        )
    }
}
