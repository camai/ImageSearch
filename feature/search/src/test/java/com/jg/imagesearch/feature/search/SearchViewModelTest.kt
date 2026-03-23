package com.jg.imagesearch.feature.search

import app.cash.turbine.test
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.SearchLocalImagesUseCase
import com.jg.imagesearch.core.domain.usecase.ToggleBookmarkUseCase
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var searchLocalImagesUseCase: SearchLocalImagesUseCase
    private lateinit var getBookmarksUseCase: GetBookmarksUseCase
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase
    private lateinit var viewModel: SearchViewModel

    private val testItem = ImageItem("테스트", "https://example.com/1.jpg", "thumb", 100, 100, false)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchLocalImagesUseCase = mockk()
        getBookmarksUseCase = mockk()
        toggleBookmarkUseCase = mockk()
        every { getBookmarksUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기_검색어는_빈_문자열이다`() = runTest {
        viewModel = SearchViewModel(searchLocalImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.query.test {
            assertEquals("", awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `검색어_변경_시_query_상태가_즉시_업데이트된다`() = runTest {
        viewModel = SearchViewModel(searchLocalImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.query.test {
            assertEquals("", awaitItem())
            viewModel.onQueryChanged("만화")
            assertEquals("만화", awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `북마크_토글_성공_시_추가_메시지가_표시된다`() = runTest {
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Success(true)
        viewModel = SearchViewModel(searchLocalImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.uiEffect.test {
            viewModel.toggleBookmark(testItem)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.BOOKMARK_ADDED, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `북마크_토글_실패_시_에러_메시지가_표시된다`() = runTest {
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Fail("DB 에러")
        viewModel = SearchViewModel(searchLocalImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.uiEffect.test {
            viewModel.toggleBookmark(testItem)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.ERROR_DEFAULT, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `이미_북마크된_아이템_토글_시_제거_메시지가_표시된다`() = runTest {
        val bookmarkedItem = testItem.copy(isBookmarked = true)
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Success(true)
        viewModel = SearchViewModel(searchLocalImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

        viewModel.uiEffect.test {
            viewModel.toggleBookmark(bookmarkedItem)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.BOOKMARK_REMOVED, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }
}
