package com.nearaid.core.database.di

import com.nearaid.core.database.NearAidDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/** On-disk name of the SQLite database, shared across platforms. */
const val DATABASE_FILE_NAME: String = "nearaid.db"

/**
 * Provides the fully built [NearAidDatabase] — platform-specific because the builder,
 * the on-disk path and the query dispatcher differ (Android context/`Dispatchers.IO`,
 * iOS `NSDocumentDirectory`/`Dispatchers.Default`). The bundled SQLite driver is common.
 */
expect val databasePlatformModule: Module

val databaseModule = module {
    includes(databasePlatformModule)
    single { get<NearAidDatabase>().listingCacheDao() }
    single { get<NearAidDatabase>().conversationCacheDao() }
}
