package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.FakeUserRepository
import com.nearaid.core.domain.sampleMe
import com.nearaid.core.model.AppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserUseCasesTest {

    @Test
    fun observeCurrentUser_reflects_the_repository_flow() = runTest {
        val repo = FakeUserRepository()
        val useCase = ObserveCurrentUserUseCase(repo)
        assertNull(useCase.invoke().first())
        val me = sampleMe()
        repo.me.value = me
        assertEquals(me, useCase.invoke().first())
    }

    @Test
    fun updateProfile_trims_display_name_and_default_area() = runTest {
        val repo = FakeUserRepository()
        UpdateProfileUseCase(repo).invoke(displayName = "  Rahim  ", defaultArea = "  Dhaka ")
        assertEquals("Rahim", repo.lastDisplayName)
        assertEquals("Dhaka", repo.lastDefaultArea)
    }

    @Test
    fun updateProfile_trims_email_and_maps_blank_to_null() = runTest {
        val repo = FakeUserRepository()
        UpdateProfileUseCase(repo).invoke(email = "  a@b.com ")
        assertEquals("a@b.com", repo.lastEmail)

        val repo2 = FakeUserRepository()
        UpdateProfileUseCase(repo2).invoke(email = "   ")
        assertNull(repo2.lastEmail)
    }

    @Test
    fun updateProfile_forwards_language_and_photo_untouched() = runTest {
        val repo = FakeUserRepository()
        UpdateProfileUseCase(repo).invoke(language = AppLanguage.EN, photoUrl = "http://x/y.png")
        assertEquals(AppLanguage.EN, repo.lastLanguage)
        assertEquals("http://x/y.png", repo.lastPhotoUrl)
    }

    @Test
    fun updateProfile_leaves_omitted_fields_null() = runTest {
        val repo = FakeUserRepository()
        UpdateProfileUseCase(repo).invoke()
        assertNull(repo.lastDisplayName)
        assertNull(repo.lastLanguage)
        assertNull(repo.lastPhotoUrl)
        assertNull(repo.lastDefaultArea)
        assertNull(repo.lastEmail)
    }

    @Test
    fun getPublicUser_and_getUserRatings_forward_args() = runTest {
        val repo = FakeUserRepository()
        GetPublicUserUseCase(repo).invoke("u9")
        assertEquals("u9", repo.lastPublicUserId)

        GetUserRatingsUseCase(repo).invoke("u9", "cursor-2")
        assertEquals("u9", repo.lastRatingsId)
        assertEquals("cursor-2", repo.lastRatingsCursor)
    }

    @Test
    fun getUserRatings_defaults_cursor_to_null() = runTest {
        val repo = FakeUserRepository()
        GetUserRatingsUseCase(repo).invoke("u9")
        assertNull(repo.lastRatingsCursor)
    }

    @Test
    fun submitVerification_and_registerDevice_forward_args() = runTest {
        val repo = FakeUserRepository()
        SubmitVerificationUseCase(repo).invoke("/tmp/id.jpg")
        assertEquals("/tmp/id.jpg", repo.lastDocumentPath)

        RegisterDeviceUseCase(repo).invoke("fcm-token")
        assertEquals("fcm-token", repo.lastFcmToken)
    }
}
