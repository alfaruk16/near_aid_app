package com.nearaid.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nearaid.core.datastore.IosSecureTokenStore
import com.nearaid.core.datastore.SecureTokenStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val dataStorePlatformModule: Module = module {
    single<DataStore<Preferences>> {
        createPreferenceDataStore(iosDataStorePath())
    }
    single<SecureTokenStore> { IosSecureTokenStore() }
}

@OptIn(ExperimentalForeignApi::class)
private fun iosDataStorePath(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path) + "/" + DATASTORE_FILE_NAME
}
