package com.nearaid.core.network.interceptor

import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.model.AuthTokens
import com.nearaid.core.network.api.AuthApi
import com.nearaid.core.network.dto.TokenRefreshResponseDto
import dagger.Lazy
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TokenAuthenticatorTest {

    private val prefs = mockk<AuthPreferencesDataSource>()
    private val api = mockk<AuthApi>()
    private val lazyApi = mockk<Lazy<AuthApi>> { every { get() } returns api }

    private fun authenticator() = TokenAuthenticator(prefs, lazyApi)

    private fun response(authorization: String?, priorDepth: Int = 0): Response {
        val builder = Request.Builder().url("http://host/v1/me")
        if (authorization != null) builder.header("Authorization", authorization)
        val request = builder.build()
        fun resp(prior: Response?) = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .apply { if (prior != null) priorResponse(prior) }
            .build()
        var r = resp(null)
        repeat(priorDepth) { r = resp(r) }
        return r
    }

    @Test
    fun gives_up_after_two_attempts() {
        assertNull(authenticator().authenticate(null, response(authorization = "Bearer x", priorDepth = 1)))
    }

    @Test
    fun returns_null_when_there_is_no_session() {
        coEvery { prefs.currentTokens() } returns null
        assertNull(authenticator().authenticate(null, response(authorization = "Bearer x")))
    }

    @Test
    fun retries_with_current_token_when_another_thread_already_refreshed() {
        coEvery { prefs.currentTokens() } returns AuthTokens(accessToken = "fresh", refreshToken = "r")

        val retried = authenticator().authenticate(null, response(authorization = "Bearer stale"))

        assertEquals("Bearer fresh", retried?.header("Authorization"))
        coVerify(exactly = 0) { api.refresh(any()) } // no network refresh needed
    }

    @Test
    fun refreshes_persists_and_retries_on_success() {
        coEvery { prefs.currentTokens() } returns AuthTokens(accessToken = "old", refreshToken = "r")
        coEvery { api.refresh(any()) } returns TokenRefreshResponseDto(access = "new")
        coEvery { prefs.updateAccessToken("new") } just Runs

        val retried = authenticator().authenticate(null, response(authorization = "Bearer old"))

        assertEquals("Bearer new", retried?.header("Authorization"))
        coVerify(exactly = 1) { prefs.updateAccessToken("new") }
    }

    @Test
    fun clears_session_and_gives_up_when_refresh_fails() {
        coEvery { prefs.currentTokens() } returns AuthTokens(accessToken = "old", refreshToken = "r")
        coEvery { api.refresh(any()) } throws RuntimeException("500")
        coEvery { prefs.clear() } just Runs

        val retried = authenticator().authenticate(null, response(authorization = "Bearer old"))

        assertNull(retried)
        coVerify(exactly = 1) { prefs.clear() }
    }
}
