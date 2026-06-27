package com.nearaid.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nearaid.core.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the JWT pair in DataStore Preferences and exposes session state as a
 * [Flow]. The network layer reads tokens from here to attach the `Authorization`
 * header and to refresh (§9.2).
 */
@Singleton
class AuthPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val ACCESS = stringPreferencesKey("access_token")
        val REFRESH = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    val tokens: Flow<AuthTokens?> = dataStore.data.map { prefs ->
        val access = prefs[Keys.ACCESS]
        val refresh = prefs[Keys.REFRESH]
        if (access != null && refresh != null) AuthTokens(access, refresh) else null
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[Keys.ACCESS] != null }

    suspend fun userId(): String? = dataStore.data.first()[Keys.USER_ID]

    suspend fun saveSession(tokens: AuthTokens, userId: String) {
        dataStore.edit { prefs ->
            prefs[Keys.ACCESS] = tokens.accessToken
            prefs[Keys.REFRESH] = tokens.refreshToken
            prefs[Keys.USER_ID] = userId
        }
    }

    suspend fun updateAccessToken(accessToken: String) {
        dataStore.edit { prefs -> prefs[Keys.ACCESS] = accessToken }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS)
            prefs.remove(Keys.REFRESH)
            prefs.remove(Keys.USER_ID)
        }
    }

    suspend fun currentTokens(): AuthTokens? = tokens.first()
}
