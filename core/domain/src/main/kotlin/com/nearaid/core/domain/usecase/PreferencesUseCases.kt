package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.repository.PreferencesRepository
import com.nearaid.core.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLanguageUseCase @Inject constructor(
    private val repository: PreferencesRepository,
) {
    operator fun invoke(): Flow<AppLanguage> = repository.language
}

class SetLanguageUseCase @Inject constructor(
    private val repository: PreferencesRepository,
) {
    suspend operator fun invoke(language: AppLanguage) = repository.setLanguage(language)
}

class ObserveSearchRadiusUseCase @Inject constructor(
    private val repository: PreferencesRepository,
) {
    operator fun invoke(): Flow<Double> = repository.searchRadiusKm
}

class SetSearchRadiusUseCase @Inject constructor(
    private val repository: PreferencesRepository,
) {
    suspend operator fun invoke(radiusKm: Double) = repository.setSearchRadiusKm(radiusKm)
}
