package com.nearaid.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nearaid.core.datastore.AndroidSecureTokenStore
import com.nearaid.core.datastore.SecureTokenStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataStorePlatformModule: Module = module {
    single<DataStore<Preferences>> {
        createPreferenceDataStore(
            androidContext().filesDir.resolve("datastore/$DATASTORE_FILE_NAME").absolutePath,
        )
    }
    single<SecureTokenStore> { AndroidSecureTokenStore(androidContext()) }
}
