package com.jg.imagesearch.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_remote_keys")
data class SearchRemoteKeysEntity(
    @PrimaryKey val link: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val searchQuery: String
)
