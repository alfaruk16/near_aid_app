package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.network.api.AuthApi
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AuthRepositoryImplTest {

    private fun prefs(store: FakeSecureTokenStore = FakeSecureTokenStore()) =
        AuthPreferencesDataSource(store)

    @Test
    fun requestOtp_maps_the_challenge() = runTest {
        val api = AuthApi(testClient { """{"request_id":"req-9","expires_in":90}""" })
        val repo = AuthRepositoryImpl(api, prefs(), testDispatcher)
        val result = repo.requestOtp("+8801712345678")
        assertIs<DataResult.Success<*>>(result)
        assertEquals("req-9", (result as DataResult.Success).data.requestId)
        assertEquals(90, result.data.expiresInSeconds)
    }

    @Test
    fun verifyOtp_persists_the_session_and_flips_isLoggedIn() = runTest {
        val store = FakeSecureTokenStore()
        val prefs = prefs(store)
        val api = AuthApi(
            testClient {
                """{"access_token":"acc","refresh_token":"ref","is_new_user":true,"user":{"id":"u1"}}"""
            }
        )
        val repo = AuthRepositoryImpl(api, prefs, testDispatcher)
        assertFalse(prefs.isLoggedIn.first())

        val result = repo.verifyOtp("req-9", "123456")
        assertIs<DataResult.Success<*>>(result)
        val session = (result as DataResult.Success).data
        assertTrue(session.isNewUser)
        assertEquals("u1", session.userId)
        // Session persisted to the secure store and reflected in the login flow.
        assertEquals("acc", store.session?.accessToken)
        assertTrue(prefs.isLoggedIn.first())
    }

    @Test
    fun verifyOtp_does_not_persist_on_failure() = runTest {
        val store = FakeSecureTokenStore()
        val repo = AuthRepositoryImpl(AuthApi(failingClient(HttpStatusCode.BadRequest)), prefs(store), testDispatcher)
        assertIs<DataResult.Failure>(repo.verifyOtp("req-9", "000000"))
        assertEquals(null, store.session)
    }

    @Test
    fun logout_clears_the_session_even_if_the_api_call_fails() = runTest {
        val store = FakeSecureTokenStore(
            com.nearaid.core.datastore.StoredSession("acc", "ref", "u1"),
        )
        val prefs = prefs(store)
        // API returns 500 on logout; repo must still clear local state (runCatching).
        val repo = AuthRepositoryImpl(AuthApi(failingClient(HttpStatusCode.InternalServerError)), prefs, testDispatcher)
        assertTrue(prefs.isLoggedIn.first())
        repo.logout()
        assertEquals(1, store.clearCalls)
        assertFalse(prefs.isLoggedIn.first())
    }
}
