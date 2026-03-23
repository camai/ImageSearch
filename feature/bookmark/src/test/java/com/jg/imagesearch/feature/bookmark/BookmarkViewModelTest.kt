package com.jg.imagesearch.feature.bookmark

import app.cash.turbine.test
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.RemoveBookmarksUseCase
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem
import com.jg.imagesearch.core.model.SnackbarMessage
import com.jg.imagesearch.core.model.UiEffect
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getBookmarksUseCase: GetBookmarksUseCase
    private lateinit var removeBookmarksUseCase: RemoveBookmarksUseCase
    private lateinit var viewModel: BookmarkViewModel

    private val testBookmarks = listOf(
        ImageItem("북마크1", "https://example.com/1.jpg", "thumb1", 100, 100, true),
        ImageItem("북마크2", "https://example.com/2.jpg", "thumb2", 100, 100, true),
        ImageItem("북마크3", "https://example.com/3.jpg", "thumb3", 100, 100, true)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getBookmarksUseCase = mockk()
        removeBookmarksUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `북마크_목록이_Flow로_정상적으로_수집된다`() = runTest {
        every { getBookmarksUseCase() } returns flowOf(testBookmarks)
        viewModel = BookmarkViewModel(getBookmarksUseCase, removeBookmarksUseCase)

        viewModel.bookmarks.test {
            // 초기값(emptyList) 또는 실제 데이터를 받을 때까지 대기
            var bookmarks = awaitItem()
            if (bookmarks.isEmpty()) {
                bookmarks = awaitItem()
            }
            assertEquals(3, bookmarks.size)
            assertEquals("북마크1", bookmarks[0].title)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `북마크가_없으면_빈_리스트가_반환된다`() = runTest {
        every { getBookmarksUseCase() } returns flowOf(emptyList())
        viewModel = BookmarkViewModel(getBookmarksUseCase, removeBookmarksUseCase)
        advanceUntilIdle()

        viewModel.bookmarks.test {
            val bookmarks = awaitItem()
            assertTrue(bookmarks.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `북마크_삭제_성공_시_삭제_건수가_포함된_스낵바가_표시된다`() = runTest {
        every { getBookmarksUseCase() } returns flowOf(testBookmarks)
        coEvery { removeBookmarksUseCase(any()) } returns DomainResult.Success(true)
        viewModel = BookmarkViewModel(getBookmarksUseCase, removeBookmarksUseCase)
        advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.removeBookmarks(testBookmarks.take(2))
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            val snackbar = effect as UiEffect.ShowSnackbar
            assertEquals(SnackbarMessage.BOOKMARKS_DELETED, snackbar.message)
            assertEquals("2", snackbar.args[0])
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `북마크_삭제_실패_시_에러_스낵바가_표시된다`() = runTest {
        every { getBookmarksUseCase() } returns flowOf(testBookmarks)
        coEvery { removeBookmarksUseCase(any()) } returns DomainResult.Fail("DB 오류")
        viewModel = BookmarkViewModel(getBookmarksUseCase, removeBookmarksUseCase)
        advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.removeBookmarks(testBookmarks)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.ERROR_DEFAULT, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }
}
