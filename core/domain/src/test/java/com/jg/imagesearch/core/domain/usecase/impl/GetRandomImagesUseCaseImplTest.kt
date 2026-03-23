package com.jg.imagesearch.core.domain.usecase.impl

import com.jg.imagesearch.core.domain.repository.ImageRepository
import com.jg.imagesearch.core.model.DataResult
import com.jg.imagesearch.core.model.DomainResult
import com.jg.imagesearch.core.model.ImageItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRandomImagesUseCaseImplTest {

    private lateinit var repository: ImageRepository
    private lateinit var useCase: GetRandomImagesUseCaseImpl

    private val testImages = List(50) { i ->
        ImageItem("이미지$i", "https://example.com/$i.jpg", "thumb$i", 100, 100)
    }

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetRandomImagesUseCaseImpl(repository)
    }

    @Test
    fun `랜덤_이미지_조회_성공_시_이미지_리스트를_반환한다`() = runTest {
        coEvery { repository.getRandomImages("만화", 50) } returns DataResult.Success(testImages)

        val result = useCase("만화", 50)

        assertTrue(result is DomainResult.Success)
        assertEquals(50, (result as DomainResult.Success).data.size)
    }

    @Test
    fun `API_호출_실패_시_에러_메시지와_함께_Fail을_반환한다`() = runTest {
        coEvery { repository.getRandomImages("만화", 50) } returns DataResult.Fail("네트워크 오류")

        val result = useCase("만화", 50)

        assertTrue(result is DomainResult.Fail)
        assertEquals("네트워크 오류", (result as DomainResult.Fail).error)
    }

    @Test
    fun `빈_검색_결과_시_빈_리스트를_반환한다`() = runTest {
        coEvery { repository.getRandomImages("존재하지않는키워드", 50) } returns DataResult.Success(emptyList())

        val result = useCase("존재하지않는키워드", 50)

        assertTrue(result is DomainResult.Success)
        assertTrue((result as DomainResult.Success).data.isEmpty())
    }
}
