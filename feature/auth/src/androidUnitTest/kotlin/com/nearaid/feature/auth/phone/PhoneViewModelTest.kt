package com.nearaid.feature.auth.phone

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.RequestOtpUseCase
import com.nearaid.core.model.OtpChallenge
import com.nearaid.feature.auth.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val requestOtp = mockk<RequestOtpUseCase>()

    private fun viewModel() = PhoneViewModel(requestOtp = requestOtp)

    // --- reducer -------------------------------------------------------------

    @Test
    fun `PhoneChanged keeps only digits and caps at ten`() {
        val viewModel = viewModel()

        viewModel.onIntent(PhoneIntent.PhoneChanged("+1 (712) 345-678900"))

        assertEquals("1712345678", viewModel.state.value.phone)
    }

    @Test
    fun `PhoneChanged clears a previous error`() {
        coEvery { requestOtp(any()) } returns DataResult.Failure(AppError.Network("boom"))
        val viewModel = viewModel()
        viewModel.onIntent(PhoneIntent.PhoneChanged("1712345678"))
        viewModel.onIntent(PhoneIntent.Submit)
        assertEquals("boom", viewModel.state.value.error)

        viewModel.onIntent(PhoneIntent.PhoneChanged("1712345679"))

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `canSubmit is false for an incomplete number and true for a valid one`() {
        val viewModel = viewModel()

        viewModel.onIntent(PhoneIntent.PhoneChanged("1712"))
        assertFalse(viewModel.state.value.canSubmit)

        viewModel.onIntent(PhoneIntent.PhoneChanged("1712345678"))
        assertTrue(viewModel.state.value.canSubmit)
    }

    // --- submit --------------------------------------------------------------

    @Test
    fun `Submit is a no-op when the number is invalid`() {
        val viewModel = viewModel()
        viewModel.onIntent(PhoneIntent.PhoneChanged("1712")) // too short

        viewModel.onIntent(PhoneIntent.Submit)

        coVerify(exactly = 0) { requestOtp(any()) }
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `Submit requests an OTP for the normalised E164 number and emits CodeSent`() = runTest {
        coEvery { requestOtp("+8801712345678") } returns
            DataResult.Success(OtpChallenge(requestId = "req-1", expiresInSeconds = 120))
        val viewModel = viewModel()
        viewModel.onIntent(PhoneIntent.PhoneChanged("1712345678"))

        viewModel.effect.test {
            viewModel.onIntent(PhoneIntent.Submit)
            assertEquals(PhoneEffect.CodeSent("req-1", "+8801712345678"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { requestOtp("+8801712345678") }
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `Submit failure surfaces the error and clears loading`() {
        coEvery { requestOtp(any()) } returns DataResult.Failure(AppError.RateLimited("slow down"))
        val viewModel = viewModel()
        viewModel.onIntent(PhoneIntent.PhoneChanged("1712345678"))

        viewModel.onIntent(PhoneIntent.Submit)

        assertEquals("slow down", viewModel.state.value.error)
        assertFalse(viewModel.state.value.loading)
    }
}
