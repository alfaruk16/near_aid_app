package com.nearaid.core.network.interceptor

import com.nearaid.core.datastore.AuthPreferencesDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches `Authorization: Bearer <access>` to every outgoing request that is not
 * itself an auth/OTP endpoint (§9).
 */
class AuthInterceptor(
    private val authPrefs: AuthPreferencesDataSource,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.encodedPath.contains("/auth/")) {
            return chain.proceed(request)
        }
        val accessToken = runBlocking { authPrefs.currentTokens()?.accessToken }
        val authed = if (accessToken != null) {
            request.newBuilder().header("Authorization", "Bearer $accessToken").build()
        } else {
            request
        }
        return chain.proceed(authed)
    }
}
