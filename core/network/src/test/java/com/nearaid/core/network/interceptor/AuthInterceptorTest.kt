package com.nearaid.core.network.interceptor

import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.model.AuthTokens
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {

    private val prefs = mockk<AuthPreferencesDataSource>()

    private fun chainFor(url: String, captured: CapturingSlot<Request>): Interceptor.Chain {
        val request = Request.Builder().url(url).build()
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.proceed(capture(captured)) } answers {
            Response.Builder()
                .request(captured.captured)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }
        return chain
    }

    @Test
    fun attaches_bearer_token_to_non_auth_requests() {
        coEvery { prefs.currentTokens() } returns AuthTokens("acc", "ref")
        val sent = slot<Request>()

        AuthInterceptor(prefs).intercept(chainFor("http://host/v1/me", sent))

        assertEquals("Bearer acc", sent.captured.header("Authorization"))
    }

    @Test
    fun skips_auth_endpoints_without_looking_up_tokens() {
        val sent = slot<Request>()

        AuthInterceptor(prefs).intercept(chainFor("http://host/v1/auth/otp/request", sent))

        assertNull(sent.captured.header("Authorization"))
    }

    @Test
    fun proceeds_unauthenticated_when_no_token() {
        coEvery { prefs.currentTokens() } returns null
        val sent = slot<Request>()

        AuthInterceptor(prefs).intercept(chainFor("http://host/v1/me", sent))

        assertNull(sent.captured.header("Authorization"))
    }
}
