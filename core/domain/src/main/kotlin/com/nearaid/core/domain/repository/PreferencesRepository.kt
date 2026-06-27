package com.nearaid.core.domain.repository

import com.nearaid.core.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val language: Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)
    val searchRadiusKm: Flow<Double>
    suspend fun setSearchRadiusKm(radius: Double)
}
