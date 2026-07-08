package com.nearaid.core.database.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.nearaid.core.database.NearAidDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val databasePlatformModule: Module = module {
    single<NearAidDatabase> {
        val dbFile = androidContext().getDatabasePath(DATABASE_FILE_NAME)
        Room.databaseBuilder<NearAidDatabase>(androidContext(), dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}
