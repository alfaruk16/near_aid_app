package com.nearaid.core.data.repository

import com.nearaid.core.common.dispatcher.Dispatcher
import com.nearaid.core.common.dispatcher.NearAidDispatcher
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.model.AuthSession
import com.nearaid.core.model.AuthTokens
import com.nearaid.core.model.OtpChallenge
import com.nearaid.core.network.api.AuthApi
import com.nearaid.core.network.dto.OtpRequestBody
import com.nearaid.core.network.dto.OtpVerifyBody
import com.nearaid.core.network.util.safeApiCall
import com.nearaid.core.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val authPrefs: AuthPreferencesDataSource,
    @Dispatcher(NearAidDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = authPrefs.isLoggedIn

    override suspend fun requestOtp(phone: String): DataResult<OtpChallenge> =
        withContext(ioDispatcher) {
            safeApiCall {
                val res = authApi.requestOtp(OtpRequestBody(phone))
                OtpChallenge(res.requestId, res.expiresIn)
            }
        }

    override suspend fun verifyOtp(requestId: String, code: String): DataResult<AuthSession> =
        withContext(ioDispatcher) {
            safeApiCall {
                val res = authApi.verifyOtp(OtpVerifyBody(requestId, code))
                authPrefs.saveSession(AuthTokens(res.accessToken, res.refreshToken), res.user.id)
                AuthSession(res.accessToken, res.refreshToken, res.isNewUser, res.user.id)
            }
        }

    override suspend fun logout() {
        withContext(ioDispatcher) {
            runCatching { authApi.logout() }
            authPrefs.clear()
        }
    }
}
