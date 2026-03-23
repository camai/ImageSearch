package com.jg.imagesearch.feature.search

import app.cash.turbine.test
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.SearchImagesUseCase
import com.jg.imagesearch.core.domain.usecase.ToggleBookmarkUseCase
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem
import com.jg.imagesearch.core.model.SnackbarMessage
import com.jg.imagesearch.core.model.UiEffect
import io.mockk.coEvery
import io.mockk.coVerify
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
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var searchImagesUseCase: SearchImagesUseCase
    private lateinit var getBookmarksUseCase: GetBookmarksUseCase
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase
    private lateinit var viewModel: MainViewModel

    private val testItem = ImageItem("테스트", "https://example.com/1.jpg", "thumb", 100, 100, false)
    private val bookmarkedItem = ImageItem("북마크됨", "https://example.com/2.jpg", "thumb2", 100, 100, true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchImagesUseCase = mockk()
        getBookmarksUseCase = mockk()
        toggleBookmarkUseCase = mockk()
        every { getBookmarksUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `북마크_토글_성공_시_추가_메시지가_표시된다`() = runTest {
        every { searchImagesUseCase(any()) } returns flowOf()
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Success(true)
        viewModel = MainViewModel(searchImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.uiEffect.test {
            viewModel.toggleBookmark(testItem)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.BOOKMARK_ADDED, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `이미_북마크된_아이템_토글_시_제거_메시지가_표시된다`() = runTest {
        every { searchImagesUseCase(any()) } returns flowOf()
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Success(true)
        viewModel = MainViewModel(searchImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.uiEffect.test {
            viewModel.toggleBookmark(bookmarkedItem)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.BOOKMARK_REMOVED, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `북마크_토글_실패_시_에러_메시지가_표시된다`() = runTest {
        every { searchImagesUseCase(any()) } returns flowOf()
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Fail("오류 발생")
        viewModel = MainViewModel(searchImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.uiEffect.test {
            viewModel.toggleBookmark(testItem)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.ERROR_DEFAULT, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `다중_북마크_추가_시_북마크되지_않은_아이템만_처리된다`() = runTest {
        every { searchImagesUseCase(any()) } returns flowOf()
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Success(true)
        viewModel = MainViewModel(searchImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        val items = listOf(testItem, bookmarkedItem)
        viewModel.uiEffect.test {
            viewModel.bookmarkAll(items)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.BOOKMARKS_ADDED, (effect as UiEffect.ShowSnackbar).message)
            assertEquals("1", (effect as UiEffect.ShowSnackbar).args[0])
            cancelAndConsumeRemainingEvents()
        }
        coVerify(exactly = 1) { toggleBookmarkUseCase(any()) }
    }

    @Test
    fun `다중_북마크_추가_시_모두_이미_북마크된_경우_스낵바가_표시되지_않는다`() = runTest {
        every { searchImagesUseCase(any()) } returns flowOf()
        viewModel = MainViewModel(searchImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.bookmarkAll(listOf(bookmarkedItem))
        advanceUntilIdle()

        coVerify(exactly = 0) { toggleBookmarkUseCase(any()) }
    }

    @Test
    fun `다중_북마크_추가_중_일부_실패_시_성공_건수만_보고된다`() = runTest {
        every { searchImagesUseCase(any()) } returns flowOf()
        val item1 = testItem.copy(link = "link1")
        val item2 = testItem.copy(link = "link2")
        coEvery { toggleBookmarkUseCase(item1) } returns DomainResult.Success(true)
        coEvery { toggleBookmarkUseCase(item2) } returns DomainResult.Fail("실패")
        viewModel = MainViewModel(searchImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.uiEffect.test {
            viewModel.bookmarkAll(listOf(item1, item2))
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals("1", (effect as UiEffect.ShowSnackbar).args[0])
            cancelAndConsumeRemainingEvents()
        }
    }
}
