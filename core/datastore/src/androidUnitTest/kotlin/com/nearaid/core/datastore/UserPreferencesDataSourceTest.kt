package com.nearaid.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.nearaid.core.model.AppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.junit.After
import org.junit.Before
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class UserPreferencesDataSourceTest {

    private lateinit var file: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var source: UserPreferencesDataSource

    @Before
    fun setUp() {
        file = File.createTempFile("user_prefs", ".preferences_pb").also { it.delete() }
        dataStore = PreferenceDataStoreFactory.createWithPath { file.absolutePath.toPath() }
        source = UserPreferencesDataSource(dataStore)
    }

    @After
    fun tearDown() {
        file.delete()
    }

    @Test
    fun language_defaults_to_BN_when_unset() = runTest {
        assertEquals(AppLanguage.BN, source.language.first())
    }

    @Test
    fun setLanguage_persists_and_is_observable() = runTest {
        source.setLanguage(AppLanguage.EN)
        assertEquals(AppLanguage.EN, source.language.first())

        source.setLanguage(AppLanguage.BN)
        assertEquals(AppLanguage.BN, source.language.first())
    }

    @Test
    fun searchRadius_defaults_to_5km_when_unset() = runTest {
        assertEquals(5.0, source.searchRadiusKm.first())
    }

    @Test
    fun setSearchRadius_persists_and_is_observable() = runTest {
        source.setSearchRadiusKm(12.5)
        assertEquals(12.5, source.searchRadiusKm.first())
    }

    @Test
    fun a_fresh_source_reads_values_previously_written_to_the_same_store() = runTest {
        source.setLanguage(AppLanguage.EN)
        source.setSearchRadiusKm(8.0)

        val reopened = UserPreferencesDataSource(dataStore)
        assertEquals(AppLanguage.EN, reopened.language.first())
        assertEquals(8.0, reopened.searchRadiusKm.first())
    }
}
