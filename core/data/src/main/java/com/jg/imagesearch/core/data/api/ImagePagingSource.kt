package com.jg.imagesearch.core.data.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jg.imagesearch.core.model.ImageItem

class ImagePagingSource(
    private val api: NaverImageApi,
    private val query: String
) : PagingSource<Int, ImageItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        val position = params.key ?: 1
        return runCatching {
            val response = api.searchImages(
                query = query,
                display = params.loadSize,
                start = position
            )
            val items = response.items.map { it.toDomainModel() }

            val nextKey = if (items.isEmpty()) {
                null
            } else {
                position + params.loadSize
            }

            LoadResult.Page(
                data = items,
                prevKey = if (position == 1) null else position - params.loadSize,
                nextKey = nextKey
            )
        }.getOrElse { e ->
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(state.config.pageSize) ?: anchorPage?.nextKey?.minus(state.config.pageSize)
        }
    }
}
