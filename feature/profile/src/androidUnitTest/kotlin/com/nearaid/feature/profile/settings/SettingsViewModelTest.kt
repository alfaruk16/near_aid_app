package com.nearaid.feature.profile.settings

import app.cash.turbine.test
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.LogoutUseCase
import com.nearaid.core.domain.usecase.ObserveLanguageUseCase
import com.nearaid.core.domain.usecase.SetLanguageUseCase
import com.nearaid.core.domain.usecase.UpdateProfileUseCase
import com.nearaid.core.model.AccountStatus
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Me
import com.nearaid.feature.profile.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val logout = mockk<LogoutUseCase>()
    private val observeLanguage = mockk<ObserveLanguageUseCase>()
    private val setLanguage = mockk<SetLanguageUseCase>()
    private val updateProfile = mockk<UpdateProfileUseCase>()

    @Before
    fun setUp() {
        every { observeLanguage() } returns flowOf(AppLanguage.BN)
        coEvery { setLanguage(any()) } returns Unit
        coEvery { updateProfile(language = any()) } returns DataResult.Success(me())
        coEvery { logout() } returns Unit
    }

    private fun viewModel() = SettingsViewModel(
        logout = logout,
        observeLanguage = observeLanguage,
        setLanguage = setLanguage,
        updateProfile = updateProfile,
    )

    @Test
    fun `observes the current language into state on init`() {
        every { observeLanguage() } returns flowOf(AppLanguage.EN)

        val state = viewModel().state.value

        assertEquals(AppLanguage.EN, state.language)
    }

    @Test
    fun `SelectLanguage persists the choice and updates the profile`() {
        val viewModel = viewModel()

        viewModel.onIntent(SettingsIntent.SelectLanguage(AppLanguage.EN))

        coVerify(exactly = 1) { setLanguage(AppLanguage.EN) }
        coVerify(exactly = 1) { updateProfile(language = AppLanguage.EN) }
    }

    @Test
    fun `LogOut invokes logout clears loading and emits LoggedOut`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(SettingsIntent.LogOut)
            assertEquals(SettingsEffect.LoggedOut, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { logout() }
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `BackClicked emits NavigateBack`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(SettingsIntent.BackClicked)
            assertEquals(SettingsEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun me() = Me(
        id = "me",
        phone = "+8801712345678",
        email = null,
        displayName = "Rahim",
        photoUrl = null,
        language = AppLanguage.EN,
        defaultArea = null,
        isPhoneVerified = true,
        isIdVerified = false,
        trustScore = 4.5,
        status = AccountStatus.ACTIVE,
    )
}
