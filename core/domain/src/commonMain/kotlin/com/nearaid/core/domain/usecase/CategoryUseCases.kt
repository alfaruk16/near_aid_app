package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.CategoryRepository
import com.nearaid.core.model.Category
import kotlinx.coroutines.flow.Flow

class ObserveCategoriesUseCase(
    private val repository: CategoryRepository,
) {
    operator fun invoke(): Flow<List<Category>> = repository.observeCategories()
}

class RefreshCategoriesUseCase(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(): DataResult<List<Category>> = repository.refreshCategories()
}
