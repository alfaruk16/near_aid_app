package com.nearaid.core.datastore

import com.nearaid.core.model.AuthTokens
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeSecureTokenStore(initial: StoredSession? = null) : SecureTokenStore {
    var session: StoredSession? = initial
    var clearCalls = 0

    override fun readSession(): StoredSession? = session
    override fun save(session: StoredSession) { this.session = session }
    override fun updateAccessToken(accessToken: String) {
        session = session?.copy(accessToken = accessToken)
    }
    override fun clear() {
        clearCalls++
        session = null
    }
}

class AuthPreferencesDataSourceTest {

    @Test
    fun seeds_its_state_from_the_secure_store_at_construction() = runTest {
        val store = FakeSecureTokenStore(StoredSession("acc", "ref", "u1"))
        val source = AuthPreferencesDataSource(store)
        assertTrue(source.isLoggedIn.first())
        assertEquals(AuthTokens("acc", "ref"), source.tokens.first())
        assertEquals("u1", source.userId())
    }

    @Test
    fun starts_logged_out_with_no_stored_session() = runTest {
        val source = AuthPreferencesDataSource(FakeSecureTokenStore())
        assertFalse(source.isLoggedIn.first())
        assertNull(source.tokens.first())
        assertNull(source.userId())
        assertNull(source.currentTokens())
    }

    @Test
    fun saveSession_persists_to_store_and_updates_the_reactive_state() = runTest {
        val store = FakeSecureTokenStore()
        val source = AuthPreferencesDataSource(store)

        source.saveSession(AuthTokens("acc", "ref"), "u9")

        assertEquals(StoredSession("acc", "ref", "u9"), store.session)
        assertTrue(source.isLoggedIn.first())
        assertEquals("u9", source.userId())
        assertEquals(AuthTokens("acc", "ref"), source.currentTokens())
    }

    @Test
    fun updateAccessToken_rotates_only_the_access_token() = runTest {
        val store = FakeSecureTokenStore(StoredSession("old", "ref", "u1"))
        val source = AuthPreferencesDataSource(store)

        source.updateAccessToken("new")

        assertEquals("new", store.session?.accessToken)
        assertEquals("ref", store.session?.refreshToken)
        assertEquals(AuthTokens("new", "ref"), source.currentTokens())
    }

    @Test
    fun updateAccessToken_is_a_noop_when_logged_out() = runTest {
        val source = AuthPreferencesDataSource(FakeSecureTokenStore())
        source.updateAccessToken("new")
        assertNull(source.currentTokens())
    }

    @Test
    fun clear_wipes_store_and_flips_state_to_logged_out() = runTest {
        val store = FakeSecureTokenStore(StoredSession("acc", "ref", "u1"))
        val source = AuthPreferencesDataSource(store)

        source.clear()

        assertEquals(1, store.clearCalls)
        assertNull(store.session)
        assertFalse(source.isLoggedIn.first())
        assertNull(source.currentTokens())
    }
}
