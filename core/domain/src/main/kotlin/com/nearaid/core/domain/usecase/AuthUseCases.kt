package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.AuthRepository
import com.nearaid.core.model.AuthSession
import com.nearaid.core.model.OtpChallenge
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestOtpUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(phone: String): DataResult<OtpChallenge> =
        repository.requestOtp(phone.trim())
}

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(requestId: String, code: String): DataResult<AuthSession> =
        repository.verifyOtp(requestId, code.trim())
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke() = repository.logout()
}

class ObserveLoginStateUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.isLoggedIn
}
