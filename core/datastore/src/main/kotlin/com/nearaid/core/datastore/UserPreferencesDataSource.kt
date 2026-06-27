package com.nearaid.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nearaid.core.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Non-auth user preferences: language (§NFR localization) and search radius (FR-10). */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val RADIUS = doublePreferencesKey("search_radius_km")
    }

    val language: Flow<AppLanguage> =
        dataStore.data.map { AppLanguage.fromCode(it[Keys.LANGUAGE]) }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    val searchRadiusKm: Flow<Double> =
        dataStore.data.map { it[Keys.RADIUS] ?: 5.0 }

    suspend fun setSearchRadiusKm(radius: Double) {
        dataStore.edit { it[Keys.RADIUS] = radius }
    }
}
