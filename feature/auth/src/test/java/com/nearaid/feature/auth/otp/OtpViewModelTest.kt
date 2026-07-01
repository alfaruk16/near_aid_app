package com.nearaid.feature.auth.otp

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.RefreshCurrentUserUseCase
import com.nearaid.core.domain.usecase.VerifyOtpUseCase
import com.nearaid.core.model.AccountStatus
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.AuthSession
import com.nearaid.core.model.Me
import com.nearaid.feature.auth.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OtpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val verifyOtp = mockk<VerifyOtpUseCase>()
    private val refreshCurrentUser = mockk<RefreshCurrentUserUseCase>()

    @Before
    fun setUp() {
        // The verify success path calls refreshCurrentUser and ignores its result.
        coEvery { refreshCurrentUser() } returns DataResult.Success(me())
    }

    private fun viewModel() = OtpViewModel(
        verifyOtp = verifyOtp,
        refreshCurrentUser = refreshCurrentUser,
    )

    // --- reducer -------------------------------------------------------------

    @Test
    fun `CodeChanged keeps only digits and caps at six`() {
        val viewModel = viewModel()

        viewModel.onIntent(OtpIntent.CodeChanged("12a34b567890"))

        assertEquals("123456", viewModel.state.value.code)
    }

    // --- verify --------------------------------------------------------------

    @Test
    fun `Verify is a no-op until six digits are entered`() {
        val viewModel = viewModel()
        viewModel.onIntent(OtpIntent.CodeChanged("123"))

        viewModel.onIntent(OtpIntent.Verify("req-1"))

        coVerify(exactly = 0) { verifyOtp(any(), any()) }
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `Verify success refreshes the user and emits Verified with the new-user flag`() = runTest {
        coEvery { verifyOtp("req-1", "123456") } returns
            DataResult.Success(session(isNewUser = true))
        val viewModel = viewModel()
        viewModel.onIntent(OtpIntent.CodeChanged("123456"))

        viewModel.effect.test {
            viewModel.onIntent(OtpIntent.Verify("req-1"))
            assertEquals(OtpEffect.Verified(isNewUser = true), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { verifyOtp("req-1", "123456") }
        coVerify(exactly = 1) { refreshCurrentUser() }
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `Verify propagates the new-user flag when the account already exists`() = runTest {
        coEvery { verifyOtp(any(), any()) } returns DataResult.Success(session(isNewUser = false))
        val viewModel = viewModel()
        viewModel.onIntent(OtpIntent.CodeChanged("654321"))

        viewModel.effect.test {
            viewModel.onIntent(OtpIntent.Verify("req-2"))
            assertEquals(OtpEffect.Verified(isNewUser = false), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Verify failure surfaces the error and does not refresh the user`() {
        coEvery { verifyOtp(any(), any()) } returns DataResult.Failure(AppError.Validation("wrong code"))
        val viewModel = viewModel()
        viewModel.onIntent(OtpIntent.CodeChanged("000000"))

        viewModel.onIntent(OtpIntent.Verify("req-1"))

        assertEquals("wrong code", viewModel.state.value.error)
        assertFalse(viewModel.state.value.loading)
        coVerify(exactly = 0) { refreshCurrentUser() }
    }

    // --- fixtures ------------------------------------------------------------

    private fun session(isNewUser: Boolean) = AuthSession(
        accessToken = "access",
        refreshToken = "refresh",
        isNewUser = isNewUser,
        userId = "user-1",
    )

    private fun me() = Me(
        id = "user-1",
        phone = "+8801712345678",
        email = null,
        displayName = null,
        photoUrl = null,
        language = AppLanguage.BN,
        defaultArea = null,
        isPhoneVerified = true,
        isIdVerified = false,
        trustScore = 0.0,
        status = AccountStatus.ACTIVE,
    )
}
