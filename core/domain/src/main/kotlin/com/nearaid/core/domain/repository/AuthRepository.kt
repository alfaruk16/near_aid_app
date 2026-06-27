package com.nearaid.core.domain.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.AuthSession
import com.nearaid.core.model.OtpChallenge
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Whether a valid session exists locally. */
    val isLoggedIn: Flow<Boolean>
    suspend fun requestOtp(phone: String): DataResult<OtpChallenge>
    suspend fun verifyOtp(requestId: String, code: String): DataResult<AuthSession>
    suspend fun logout()
}
