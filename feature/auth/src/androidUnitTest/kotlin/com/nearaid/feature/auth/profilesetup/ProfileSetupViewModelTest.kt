package com.nearaid.feature.auth.profilesetup

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.UpdateProfileUseCase
import com.nearaid.core.model.AccountStatus
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Me
import com.nearaid.feature.auth.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileSetupViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val updateProfile = mockk<UpdateProfileUseCase>()

    private fun viewModel() = ProfileSetupViewModel(updateProfile = updateProfile)

    // --- reducer -------------------------------------------------------------

    @Test
    fun `NameChanged updates the name and clears any error`() {
        val viewModel = viewModel()
        viewModel.onIntent(ProfileSetupIntent.Finish) // blank -> sets an error
        assertEquals("Please enter a display name", viewModel.state.value.error)

        viewModel.onIntent(ProfileSetupIntent.NameChanged("Farah"))

        assertEquals("Farah", viewModel.state.value.name)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `LanguageChanged updates the selected language`() {
        val viewModel = viewModel()

        viewModel.onIntent(ProfileSetupIntent.LanguageChanged(AppLanguage.EN))

        assertEquals(AppLanguage.EN, viewModel.state.value.language)
    }

    // --- finish --------------------------------------------------------------

    @Test
    fun `Finish rejects a blank name without calling the use case`() {
        val viewModel = viewModel()
        viewModel.onIntent(ProfileSetupIntent.NameChanged("   "))

        viewModel.onIntent(ProfileSetupIntent.Finish)

        assertEquals("Please enter a display name", viewModel.state.value.error)
        coVerify(exactly = 0) { updateProfile(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `Finish updates the profile with the trimmed name and language then emits Done`() = runTest {
        coEvery { updateProfile(displayName = "Farah", language = AppLanguage.EN) } returns
            DataResult.Success(me())
        val viewModel = viewModel()
        viewModel.onIntent(ProfileSetupIntent.NameChanged("  Farah  "))
        viewModel.onIntent(ProfileSetupIntent.LanguageChanged(AppLanguage.EN))

        viewModel.effect.test {
            viewModel.onIntent(ProfileSetupIntent.Finish)
            assertEquals(ProfileSetupEffect.Done, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { updateProfile(displayName = "Farah", language = AppLanguage.EN) }
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `Finish failure surfaces the error and clears loading`() {
        coEvery { updateProfile(displayName = any(), language = any()) } returns
            DataResult.Failure(AppError.Server(500, "server down"))
        val viewModel = viewModel()
        viewModel.onIntent(ProfileSetupIntent.NameChanged("Farah"))

        viewModel.onIntent(ProfileSetupIntent.Finish)

        assertEquals("server down", viewModel.state.value.error)
        assertFalse(viewModel.state.value.loading)
    }

    // --- fixtures ------------------------------------------------------------

    private fun me() = Me(
        id = "user-1",
        phone = "+8801712345678",
        email = null,
        displayName = "Farah",
        photoUrl = null,
        language = AppLanguage.EN,
        defaultArea = null,
        isPhoneVerified = true,
        isIdVerified = false,
        trustScore = 0.0,
        status = AccountStatus.ACTIVE,
    )
}
