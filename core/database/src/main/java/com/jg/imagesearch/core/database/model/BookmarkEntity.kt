package com.jg.imagesearch.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jg.imagesearch.core.model.ImageItem

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val link: String,
    val title: String,
    val thumbnail: String,
    val sizeHeight: Int,
    val sizeWidth: Int,
    val timestamp: Long = System.currentTimeMillis()
)

fun BookmarkEntity.toDomainModel() = ImageItem(
    title = title,
    link = link,
    thumbnail = thumbnail,
    sizeHeight = sizeHeight,
    sizeWidth = sizeWidth,
    isBookmarked = true
)

fun ImageItem.toEntity() = BookmarkEntity(
    title = title,
    link = link,
    thumbnail = thumbnail,
    sizeHeight = sizeHeight,
    sizeWidth = sizeWidth
)
