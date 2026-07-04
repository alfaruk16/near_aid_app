package com.nearaid.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.datastore.UserPreferencesDataSource
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/** On-disk name of the preferences file, shared across platforms. */
const val DATASTORE_FILE_NAME: String = "nearaid_prefs.preferences_pb"

/** Builds the multiplatform preferences DataStore from an absolute file [path]. */
internal fun createPreferenceDataStore(path: String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { path.toPath() })

/**
 * Provides `DataStore<Preferences>` — platform-specific because the file path differs
 * (Android `filesDir`, iOS `NSDocumentDirectory`).
 */
expect val dataStorePlatformModule: Module

val dataStoreModule = module {
    includes(dataStorePlatformModule)
    singleOf(::AuthPreferencesDataSource)
    singleOf(::UserPreferencesDataSource)
}
