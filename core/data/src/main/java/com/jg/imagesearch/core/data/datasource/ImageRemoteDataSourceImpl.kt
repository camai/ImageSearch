package com.jg.imagesearch.core.data.datasource

import com.jg.imagesearch.core.network.api.NaverImageApi
import com.jg.imagesearch.core.network.model.ImageSearchResponse
import javax.inject.Inject

class ImageRemoteDataSourceImpl @Inject constructor(
    private val api: NaverImageApi
) : ImageRemoteDataSource {
    override suspend fun searchImages(query: String, display: Int, start: Int): ImageSearchResponse {
        return api.searchImages(query = query, display = display, start = start)
    }
}
