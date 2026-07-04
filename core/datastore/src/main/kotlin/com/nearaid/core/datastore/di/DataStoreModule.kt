package com.nearaid.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.datastore.UserPreferencesDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataStoreModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile("nearaid_prefs")
        }
    }
    singleOf(::AuthPreferencesDataSource)
    singleOf(::UserPreferencesDataSource)
}
