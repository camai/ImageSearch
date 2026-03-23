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

class RemoveBookmarksUseCaseImplTest {

    private lateinit var repository: BookmarkRepository
    private lateinit var useCase: RemoveBookmarksUseCaseImpl

    private val testItems = listOf(
        ImageItem("이미지1", "https://example.com/1.jpg", "thumb1", 100, 100, true),
        ImageItem("이미지2", "https://example.com/2.jpg", "thumb2", 100, 100, true)
    )

    @Before
    fun setUp() {
        repository = mockk()
        useCase = RemoveBookmarksUseCaseImpl(repository)
    }

    @Test
    fun `다수_북마크_삭제_성공_시_Success를_반환한다`() = runTest {
        coEvery { repository.removeBookmarks(testItems) } returns DataResult.Success(true)

        val result = useCase(testItems)

        assertTrue(result is DomainResult.Success)
        coVerify { repository.removeBookmarks(testItems) }
    }

    @Test
    fun `빈_리스트_삭제_시에도_정상적으로_처리된다`() = runTest {
        coEvery { repository.removeBookmarks(emptyList()) } returns DataResult.Success(true)

        val result = useCase(emptyList())

        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun `삭제_실패_시_에러_메시지와_함께_Fail을_반환한다`() = runTest {
        coEvery { repository.removeBookmarks(testItems) } returns DataResult.Fail("삭제 중 오류 발생")

        val result = useCase(testItems)

        assertTrue(result is DomainResult.Fail)
        assertEquals("삭제 중 오류 발생", (result as DomainResult.Fail).error)
    }
}
