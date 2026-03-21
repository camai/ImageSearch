package com.jg.imagesearch.core.data.datasource

import com.jg.imagesearch.core.network.model.ImageSearchResponse

interface ImageRemoteDataSource {
    suspend fun searchImages(query: String, display: Int, start: Int): ImageSearchResponse
}
