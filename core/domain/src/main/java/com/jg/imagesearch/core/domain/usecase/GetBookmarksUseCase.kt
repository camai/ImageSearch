package com.jg.imagesearch.core.domain.usecase

import com.jg.imagesearch.core.model.ImageItem
import kotlinx.coroutines.flow.Flow

interface GetBookmarksUseCase {
    operator fun invoke(): Flow<List<ImageItem>>
}
