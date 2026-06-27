package com.nearaid.core.data.repository

import com.nearaid.core.datastore.UserPreferencesDataSource
import com.nearaid.core.domain.repository.PreferencesRepository
import com.nearaid.core.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val userPrefs: UserPreferencesDataSource,
) : PreferencesRepository {

    override val language: Flow<AppLanguage> = userPrefs.language

    override suspend fun setLanguage(language: AppLanguage) = userPrefs.setLanguage(language)

    override val searchRadiusKm: Flow<Double> = userPrefs.searchRadiusKm

    override suspend fun setSearchRadiusKm(radius: Double) = userPrefs.setSearchRadiusKm(radius)
}
