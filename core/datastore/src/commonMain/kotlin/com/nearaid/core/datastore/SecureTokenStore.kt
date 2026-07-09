package com.nearaid.core.datastore

/** The persisted session — the JWT pair plus the owning user id. */
data class StoredSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
)

/**
 * Platform-secured storage for the auth session. Unlike ordinary preferences, tokens are kept in the
 * OS-backed secure enclave — **Android** EncryptedSharedPreferences (Keystore-wrapped key) and **iOS**
 * the Keychain — so a device compromise can't trivially lift them from a plaintext prefs file.
 *
 * Reads/writes are synchronous: both backends are fast local calls, and [AuthPreferencesDataSource]
 * mirrors the value into a `StateFlow` for the reactive session state the app observes.
 */
interface SecureTokenStore {
    fun readSession(): StoredSession?
    fun save(session: StoredSession)
    fun updateAccessToken(accessToken: String)
    fun clear()
}
