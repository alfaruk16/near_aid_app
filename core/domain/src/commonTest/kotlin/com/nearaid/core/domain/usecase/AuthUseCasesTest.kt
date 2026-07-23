package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.FakeAuthRepository
import com.nearaid.core.domain.FakeUserRepository
import com.nearaid.core.domain.testError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AuthUseCasesTest {

    @Test
    fun requestOtp_trims_the_phone_before_delegating() = runTest {
        val repo = FakeAuthRepository()
        RequestOtpUseCase(repo).invoke("  +8801712345678  ")
        assertEquals("+8801712345678", repo.lastPhone)
    }

    @Test
    fun requestOtp_returns_the_repository_result() = runTest {
        val repo = FakeAuthRepository()
        val result = RequestOtpUseCase(repo).invoke("x")
        assertSame(repo.requestOtpResult, result)
    }

    @Test
    fun requestOtp_propagates_failure() = runTest {
        val repo = FakeAuthRepository().apply { requestOtpResult = DataResult.Failure(testError) }
        assertTrue(RequestOtpUseCase(repo).invoke("x") is DataResult.Failure)
    }

    @Test
    fun verifyOtp_trims_the_code_and_passes_the_request_id() = runTest {
        val repo = FakeAuthRepository()
        VerifyOtpUseCase(repo).invoke("req-9", " 123456 ")
        assertEquals("req-9", repo.lastRequestId)
        assertEquals("123456", repo.lastCode)
    }

    @Test
    fun logout_clears_both_session_and_cached_profile() = runTest {
        val auth = FakeAuthRepository()
        val user = FakeUserRepository()
        LogoutUseCase(auth, user).invoke()
        assertEquals(1, auth.logoutCalls)
        assertEquals(1, user.clearCalls)
    }

    @Test
    fun observeLoginState_reflects_the_repository_flow() = runTest {
        val repo = FakeAuthRepository()
        val useCase = ObserveLoginStateUseCase(repo)
        assertEquals(false, useCase.invoke().first())
        repo.isLoggedIn.value = true
        assertEquals(true, useCase.invoke().first())
    }
}
