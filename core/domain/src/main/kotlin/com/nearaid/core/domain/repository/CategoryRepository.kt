package com.nearaid.core.domain.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun refreshCategories(): DataResult<List<Category>>
}
