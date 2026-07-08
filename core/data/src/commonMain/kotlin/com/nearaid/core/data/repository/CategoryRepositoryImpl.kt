package com.nearaid.core.data.repository

import com.nearaid.core.network.util.safeApiCall
import com.nearaid.core.network.api.CategoryApi
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.model.Category
import com.nearaid.core.domain.repository.CategoryRepository
import com.nearaid.core.common.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryApi: CategoryApi,
    private val ioDispatcher: CoroutineDispatcher,
) : CategoryRepository {

    private val categories = MutableStateFlow<List<Category>>(emptyList())

    override fun observeCategories(): Flow<List<Category>> = categories.asStateFlow()

    override suspend fun refreshCategories(): DataResult<List<Category>> =
        withContext(ioDispatcher) {
            safeApiCall { categoryApi.getCategories().results.map { it.toDomain() } }.also {
                if (it is DataResult.Success) categories.value = it.data
            }
        }
}
