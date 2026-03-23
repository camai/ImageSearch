package com.jg.imagesearch.core.domain.usecase.impl

import com.jg.imagesearch.core.domain.repository.BookmarkRepository
import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleBookmarkUseCaseImplTest {

    private lateinit var repository: BookmarkRepository
    private lateinit var useCase: ToggleBookmarkUseCaseImpl

    private val testItem = ImageItem(
        title = "테스트 이미지",
        link = "https://example.com/image.jpg",
        thumbnail = "https://example.com/thumb.jpg",
        sizeHeight = 100,
        sizeWidth = 100,
        isBookmarked = false
    )

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ToggleBookmarkUseCaseImpl(repository)
    }

    @Test
    fun `북마크되지_않은_아이템을_토글하면_북마크가_추가된다`() = runTest {
        coEvery { repository.isBookmarked(testItem.link) } returns false
        coEvery { repository.addBookmark(any()) } returns DataResult.Success(true)

        val result = useCase(testItem)

        assertTrue(result is DomainResult.Success)
        coVerify { repository.addBookmark(testItem.copy(isBookmarked = true)) }
    }

    @Test
    fun `이미_북마크된_아이템을_토글하면_북마크가_제거된다`() = runTest {
        coEvery { repository.isBookmarked(testItem.link) } returns true
        coEvery { repository.removeBookmark(testItem) } returns DataResult.Success(true)

        val result = useCase(testItem)

        assertTrue(result is DomainResult.Success)
        coVerify { repository.removeBookmark(testItem) }
    }

    @Test
    fun `북마크_추가_실패_시_Fail_결과를_반환한다`() = runTest {
        coEvery { repository.isBookmarked(testItem.link) } returns false
        coEvery { repository.addBookmark(any()) } returns DataResult.Fail("DB Error")

        val result = useCase(testItem)

        assertTrue(result is DomainResult.Fail)
        assertEquals("DB Error", (result as DomainResult.Fail).error)
    }

    @Test
    fun `북마크_제거_실패_시_Fail_결과를_반환한다`() = runTest {
        coEvery { repository.isBookmarked(testItem.link) } returns true
        coEvery { repository.removeBookmark(testItem) } returns DataResult.Fail("삭제 실패")

        val result = useCase(testItem)

        assertTrue(result is DomainResult.Fail)
        assertEquals("삭제 실패", (result as DomainResult.Fail).error)
    }
}
