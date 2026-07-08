package com.nearaid.core.database.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.nearaid.core.database.NearAidDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val databasePlatformModule: Module = module {
    single<NearAidDatabase> {
        Room.databaseBuilder<NearAidDatabase>(name = iosDatabasePath())
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun iosDatabasePath(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path) + "/" + DATABASE_FILE_NAME
}
