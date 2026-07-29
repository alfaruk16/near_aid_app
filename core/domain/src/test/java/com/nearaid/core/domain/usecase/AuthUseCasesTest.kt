package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.repository.AuthRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthUseCasesTest {

    private val repository = mockk<AuthRepository>(relaxed = true)

    @Test
    fun requestOtp_trims_the_phone() = runTest {
        RequestOtpUseCase(repository)("  +8801700000000 ")
        coVerify(exactly = 1) { repository.requestOtp("+8801700000000") }
    }

    @Test
    fun verifyOtp_trims_the_code() = runTest {
        VerifyOtpUseCase(repository)("req-1", " 123456 ")
        coVerify(exactly = 1) { repository.verifyOtp("req-1", "123456") }
    }

    @Test
    fun logout_and_observeLoginState_delegate() = runTest {
        every { repository.isLoggedIn } returns flowOf(true)

        LogoutUseCase(repository)()
        ObserveLoginStateUseCase(repository)()

        coVerify(exactly = 1) { repository.logout() }
        verify(exactly = 1) { repository.isLoggedIn }
    }
}
