package com.nearaid.feature.profile.verification

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.SubmitVerificationUseCase
import com.nearaid.feature.profile.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VerificationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val submitVerification = mockk<SubmitVerificationUseCase>()

    @Before
    fun setUp() {
        coEvery { submitVerification(any()) } returns DataResult.Success(Unit)
    }

    private fun viewModel() = VerificationViewModel(submitVerification = submitVerification)

    @Test
    fun `DocumentPicked stores the path and clears any error`() {
        val viewModel = viewModel()

        viewModel.onIntent(VerificationIntent.DocumentPicked("/tmp/doc.jpg"))
        val state = viewModel.state.value

        assertEquals("/tmp/doc.jpg", state.documentPath)
        assertNull(state.error)
    }

    @Test
    fun `Submit does nothing without a picked document`() {
        val viewModel = viewModel()

        viewModel.onIntent(VerificationIntent.Submit)

        coVerify(exactly = 0) { submitVerification(any()) }
        assertFalse(viewModel.state.value.loading)
        assertFalse(viewModel.state.value.success)
    }

    @Test
    fun `Submit success marks the state successful`() {
        coEvery { submitVerification("/tmp/doc.jpg") } returns DataResult.Success(Unit)
        val viewModel = viewModel()

        viewModel.onIntent(VerificationIntent.DocumentPicked("/tmp/doc.jpg"))
        viewModel.onIntent(VerificationIntent.Submit)
        val state = viewModel.state.value

        coVerify(exactly = 1) { submitVerification("/tmp/doc.jpg") }
        assertTrue(state.success)
        assertFalse(state.loading)
        assertNull(state.error)
    }

    @Test
    fun `Submit failure surfaces an error`() {
        coEvery { submitVerification(any()) } returns DataResult.Failure(AppError.Server(500, "upload failed"))
        val viewModel = viewModel()

        viewModel.onIntent(VerificationIntent.DocumentPicked("/tmp/doc.jpg"))
        viewModel.onIntent(VerificationIntent.Submit)
        val state = viewModel.state.value

        assertEquals("upload failed", state.error)
        assertFalse(state.success)
        assertFalse(state.loading)
    }

    @Test
    fun `BackClicked emits NavigateBack`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(VerificationIntent.BackClicked)
            assertEquals(VerificationEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
