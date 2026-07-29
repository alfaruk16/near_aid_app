package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.repository.UserRepository
import com.nearaid.core.model.AppLanguage
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UserUseCasesTest {

    private val repository = mockk<UserRepository>(relaxed = true)

    @Test
    fun updateProfile_trims_names_and_blanks_email_to_null() = runTest {
        UpdateProfileUseCase(repository)(
            displayName = "  Ann  ",
            language = AppLanguage.EN,
            photoUrl = "p",
            defaultArea = "  Dhaka ",
            email = "   ",
        )
        coVerify(exactly = 1) {
            repository.updateProfile(
                displayName = "Ann",
                language = AppLanguage.EN,
                photoUrl = "p",
                defaultArea = "Dhaka",
                email = null,
            )
        }
    }

    @Test
    fun updateProfile_keeps_a_real_email_trimmed() = runTest {
        UpdateProfileUseCase(repository)(email = "  a@b.co ")
        coVerify(exactly = 1) {
            repository.updateProfile(displayName = null, language = null, photoUrl = null, defaultArea = null, email = "a@b.co")
        }
    }

    @Test
    fun observeCurrentUser_returns_repository_stream() = runTest {
        every { repository.observeMe() } returns flowOf(null)
        ObserveCurrentUserUseCase(repository)()
        verify(exactly = 1) { repository.observeMe() }
    }

    @Test
    fun simple_user_use_cases_delegate() = runTest {
        RefreshCurrentUserUseCase(repository)()
        GetPublicUserUseCase(repository)("u1")
        GetUserRatingsUseCase(repository)("u1", "cur")
        GetUserRatingsUseCase(repository)("u2")
        SubmitVerificationUseCase(repository)("/doc")
        RegisterDeviceUseCase(repository)("fcm")

        coVerify(exactly = 1) { repository.refreshMe() }
        coVerify(exactly = 1) { repository.getPublicUser("u1") }
        coVerify(exactly = 1) { repository.getUserRatings("u1", "cur") }
        coVerify(exactly = 1) { repository.getUserRatings("u2", null) }
        coVerify(exactly = 1) { repository.submitVerification("/doc") }
        coVerify(exactly = 1) { repository.registerDevice("fcm") }
    }
}
