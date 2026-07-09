package com.nearaid.core.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Android [SecureTokenStore] backed by EncryptedSharedPreferences: values are AES-GCM encrypted with a
 * key held in the Android Keystore, so the tokens never sit on disk in plaintext.
 */
class AndroidSecureTokenStore(private val context: Context) : SecureTokenStore {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun readSession(): StoredSession? {
        val access = prefs.getString(KEY_ACCESS, null) ?: return null
        val refresh = prefs.getString(KEY_REFRESH, null) ?: return null
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        return StoredSession(access, refresh, userId)
    }

    override fun save(session: StoredSession) {
        prefs.edit()
            .putString(KEY_ACCESS, session.accessToken)
            .putString(KEY_REFRESH, session.refreshToken)
            .putString(KEY_USER_ID, session.userId)
            .apply()
    }

    override fun updateAccessToken(accessToken: String) {
        prefs.edit().putString(KEY_ACCESS, accessToken).apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_FILE_NAME = "nearaid_secure_tokens"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_USER_ID = "user_id"
    }
}
