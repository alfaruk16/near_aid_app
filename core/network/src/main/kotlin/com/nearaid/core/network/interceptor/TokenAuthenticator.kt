package com.nearaid.core.network.interceptor

import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.network.api.AuthApi
import com.nearaid.core.network.dto.TokenRefreshRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * On a 401, refreshes the access token once via POST /auth/refresh (§9.2) and
 * retries. If refresh fails the session is cleared, forcing re-login. [AuthApi] is
 * supplied lazily (a provider lambda) to break the Retrofit ⇄ Authenticator cycle.
 */
class TokenAuthenticator(
    private val authPrefs: AuthPreferencesDataSource,
    private val authApiProvider: () -> AuthApi,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        synchronized(this) {
            val tokens = runBlocking { authPrefs.currentTokens() } ?: return null
            val attempted = response.request.header("Authorization")?.removePrefix("Bearer ")

            if (attempted != null && attempted != tokens.accessToken) {
                return response.request.retryWith(tokens.accessToken)
            }

            val refreshed = runBlocking {
                runCatching { authApiProvider().refresh(TokenRefreshRequestDto(tokens.refreshToken)) }.getOrNull()
            }

            return if (refreshed != null) {
                runBlocking { authPrefs.updateAccessToken(refreshed.access) }
                response.request.retryWith(refreshed.access)
            } else {
                runBlocking { authPrefs.clear() }
                null
            }
        }
    }

    private fun Request.retryWith(accessToken: String): Request =
        newBuilder().header("Authorization", "Bearer $accessToken").build()

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
