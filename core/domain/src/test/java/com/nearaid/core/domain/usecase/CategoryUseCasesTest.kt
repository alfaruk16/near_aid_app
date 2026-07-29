package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.Category
import com.nearaid.core.domain.repository.CategoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryUseCasesTest {

    private val repository = mockk<CategoryRepository>()

    private val categories = listOf(Category(1, "food", "Food", "খাবার", null))

    @Test
    fun observeCategories_returns_the_repository_stream() = runTest {
        every { repository.observeCategories() } returns flowOf(categories)

        val emitted = ObserveCategoriesUseCase(repository)().first()

        assertEquals(categories, emitted)
        verify(exactly = 1) { repository.observeCategories() }
    }

    @Test
    fun refreshCategories_delegates_to_the_repository() = runTest {
        coEvery { repository.refreshCategories() } returns DataResult.Success(categories)

        val result = RefreshCategoriesUseCase(repository)()

        assertEquals(DataResult.Success(categories), result)
        coVerify(exactly = 1) { repository.refreshCategories() }
    }
}
