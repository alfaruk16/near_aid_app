package com.nearaid.core.datastore

import com.nearaid.core.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Persists the JWT pair in the platform [SecureTokenStore] (Android Keystore-backed
 * EncryptedSharedPreferences / iOS Keychain) and exposes session state as a [Flow]. The network
 * layer reads tokens from here to attach the `Authorization` header and to refresh (§9.2).
 *
 * The secure backends aren't observable, so the current session is mirrored into a [MutableStateFlow]
 * — seeded from the store at construction and updated on every mutation — to preserve the reactive
 * `tokens` / `isLoggedIn` surface the rest of the app depends on.
 */
class AuthPreferencesDataSource(
    private val secureStore: SecureTokenStore,
) {
    private val session = MutableStateFlow(secureStore.readSession())

    val tokens: Flow<AuthTokens?> = session.map { it?.let { s -> AuthTokens(s.accessToken, s.refreshToken) } }

    val isLoggedIn: Flow<Boolean> = session.map { it != null }

    suspend fun userId(): String? = session.value?.userId

    suspend fun saveSession(tokens: AuthTokens, userId: String) {
        val stored = StoredSession(tokens.accessToken, tokens.refreshToken, userId)
        secureStore.save(stored)
        session.value = stored
    }

    suspend fun updateAccessToken(accessToken: String) {
        secureStore.updateAccessToken(accessToken)
        session.update { it?.copy(accessToken = accessToken) }
    }

    suspend fun clear() {
        secureStore.clear()
        session.value = null
    }

    suspend fun currentTokens(): AuthTokens? =
        session.value?.let { AuthTokens(it.accessToken, it.refreshToken) }
}
