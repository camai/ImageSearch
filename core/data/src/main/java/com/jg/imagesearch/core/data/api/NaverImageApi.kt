package com.jg.imagesearch.core.data.api

import com.jg.imagesearch.core.data.api.dto.ImageSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverImageApi {
    @GET("v1/search/image")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("display") display: Int = 50,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "sim",
        @Query("filter") filter: String = "all"
    ): ImageSearchResponse
}
