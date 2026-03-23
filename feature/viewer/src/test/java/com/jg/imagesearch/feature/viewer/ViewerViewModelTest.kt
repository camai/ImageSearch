package com.jg.imagesearch.feature.viewer

import app.cash.turbine.test
import com.jg.imagesearch.core.domain.usecase.GetBookmarksUseCase
import com.jg.imagesearch.core.domain.usecase.GetRandomImagesUseCase
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
class ViewerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getRandomImagesUseCase: GetRandomImagesUseCase
    private lateinit var getBookmarksUseCase: GetBookmarksUseCase
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase
    private lateinit var viewModel: ViewerViewModel

    private val selectedItem = ImageItem("선택된 이미지", "https://example.com/selected.jpg", "thumb", 100, 100)
    private val relatedImages = List(50) { i ->
        ImageItem("관련 이미지$i", "https://example.com/$i.jpg", "thumb$i", 100, 100)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRandomImagesUseCase = mockk()
        getBookmarksUseCase = mockk()
        toggleBookmarkUseCase = mockk()
        every { getBookmarksUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ViewerViewModel(getRandomImagesUseCase, getBookmarksUseCase, toggleBookmarkUseCase)

    @Test
    fun `초기화_성공_시_선택_이미지가_첫번째에_위치하고_관련_이미지가_추가된다`() = runTest {
        coEvery { getRandomImagesUseCase(any(), 50) } returns DomainResult.Success(relatedImages)
        viewModel = createViewModel()

        viewModel.initialize(selectedItem, "만화")
        advanceUntilIdle()

        viewModel.images.test {
            var images = awaitItem()
            if (images.isEmpty()) {
                images = awaitItem()
            }
            // initialize sets selectedItem first, then adds related images
            if (images.size == 1) {
                images = awaitItem()
            }
            assertEquals(selectedItem.link, images.first().link)
            assertTrue(images.size > 1)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `초기화_시_관련_이미지_로드_실패하면_에러_스낵바가_표시된다`() = runTest {
        coEvery { getRandomImagesUseCase(any(), 50) } returns DomainResult.Fail("네트워크 오류")
        viewModel = createViewModel()

        viewModel.uiEffect.test {
            viewModel.initialize(selectedItem, "만화")
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.ERROR_RELATED_IMAGES, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `중복_초기화_호출_시_두번째_호출은_무시된다`() = runTest {
        coEvery { getRandomImagesUseCase(any(), 50) } returns DomainResult.Success(relatedImages)
        viewModel = createViewModel()

        viewModel.initialize(selectedItem, "만화")
        advanceUntilIdle()
        viewModel.initialize(selectedItem, "만화") // 두 번째 호출은 무시
        advanceUntilIdle()

        viewModel.images.test {
            var images = awaitItem()
            if (images.isEmpty()) {
                images = awaitItem()
            }
            assertTrue(images.isNotEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `북마크_토글_성공_시_적절한_스낵바_메시지가_표시된다`() = runTest {
        coEvery { getRandomImagesUseCase(any(), 50) } returns DomainResult.Success(emptyList())
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Success(true)
        viewModel = createViewModel()

        viewModel.uiEffect.test {
            viewModel.toggleBookmark(selectedItem)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.BOOKMARK_ADDED, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `북마크_토글_실패_시_에러_스낵바가_표시된다`() = runTest {
        coEvery { getRandomImagesUseCase(any(), 50) } returns DomainResult.Success(emptyList())
        coEvery { toggleBookmarkUseCase(any()) } returns DomainResult.Fail("DB 오류")
        viewModel = createViewModel()

        viewModel.uiEffect.test {
            viewModel.toggleBookmark(selectedItem)
            val effect = awaitItem()
            assertTrue(effect is UiEffect.ShowSnackbar)
            assertEquals(SnackbarMessage.ERROR_DEFAULT, (effect as UiEffect.ShowSnackbar).message)
            cancelAndConsumeRemainingEvents()
        }
    }
}
