package com.nearaid.feature.profile.profile

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.ObserveCurrentUserUseCase
import com.nearaid.core.domain.usecase.RefreshCurrentUserUseCase
import com.nearaid.core.model.AccountStatus
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Me
import com.nearaid.feature.profile.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeCurrentUser = mockk<ObserveCurrentUserUseCase>()
    private val refreshCurrentUser = mockk<RefreshCurrentUserUseCase>()

    @Before
    fun setUp() {
        // Defaults so construction (which observes + refreshes) never throws.
        every { observeCurrentUser() } returns flowOf(null)
        coEvery { refreshCurrentUser() } returns DataResult.Success(me())
    }

    private fun viewModel() = ProfileViewModel(
        observeCurrentUser = observeCurrentUser,
        refreshCurrentUser = refreshCurrentUser,
    )

    @Test
    fun `loads the current user into state on init`() {
        val me = me("u1")
        every { observeCurrentUser() } returns flowOf(me)
        coEvery { refreshCurrentUser() } returns DataResult.Success(me)

        val state = viewModel().state.value

        assertEquals(me, state.me)
        assertFalse(state.loading)
        assertNull(state.error)
    }

    @Test
    fun `refresh failure surfaces an error and clears loading`() {
        every { observeCurrentUser() } returns flowOf(null)
        coEvery { refreshCurrentUser() } returns DataResult.Failure(AppError.Network("offline"))

        val state = viewModel().state.value

        assertEquals("offline", state.error)
        assertFalse(state.loading)
    }

    @Test
    fun `VerificationClicked emits OpenVerification`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ProfileIntent.VerificationClicked)
            assertEquals(ProfileEffect.OpenVerification, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SettingsClicked emits OpenSettings`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ProfileIntent.SettingsClicked)
            assertEquals(ProfileEffect.OpenSettings, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `RatingsClicked opens the public profile of the current user`() = runTest {
        every { observeCurrentUser() } returns flowOf(me("u42"))
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ProfileIntent.RatingsClicked)
            assertEquals(ProfileEffect.OpenPublicProfile("u42"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `RatingsClicked does nothing when the user has not loaded`() = runTest {
        every { observeCurrentUser() } returns flowOf(null)
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(ProfileIntent.RatingsClicked)
            expectNoEvents()
        }
    }

    private fun me(id: String = "me") = Me(
        id = id,
        phone = "+8801712345678",
        email = null,
        displayName = "Rahim",
        photoUrl = null,
        language = AppLanguage.BN,
        defaultArea = null,
        isPhoneVerified = true,
        isIdVerified = false,
        trustScore = 4.5,
        status = AccountStatus.ACTIVE,
    )
}
