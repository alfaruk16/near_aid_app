package com.nearaid.core.network.socket

import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.datastore.SecureTokenStore
import com.nearaid.core.datastore.StoredSession
import com.nearaid.core.network.api.mockClient
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertTrue

private class FakeSecureTokenStore(private val session: StoredSession?) : SecureTokenStore {
    override fun readSession(): StoredSession? = session
    override fun save(session: StoredSession) {}
    override fun updateAccessToken(accessToken: String) {}
    override fun clear() {}
}

class ChatSocketTest {

    private fun socket(session: StoredSession?) = ChatSocket(
        client = mockClient(), // no WebSockets plugin → upgrade fails
        json = Json { ignoreUnknownKeys = true },
        authPrefs = AuthPreferencesDataSource(FakeSecureTokenStore(session)),
        wsUrl = "ws://test/ws",
    )

    @Test
    fun observe_completes_without_emitting_when_the_upgrade_fails() = runTest {
        val emissions = socket(StoredSession("acc", "ref", "u1")).observe("thread-1").toList()
        // Realtime is best-effort: a failed WebSocket upgrade degrades to an empty, completed flow.
        assertTrue(emissions.isEmpty())
    }

    @Test
    fun observe_degrades_gracefully_even_with_no_token() = runTest {
        val emissions = socket(session = null).observe("thread-1").toList()
        assertTrue(emissions.isEmpty())
    }
}
