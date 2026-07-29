package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.datastore.UserPreferencesDataSource
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.network.api.AuthApi
import com.nearaid.core.network.api.RatingsPage
import com.nearaid.core.network.api.UserApi
import com.nearaid.core.network.dto.AuthResponse
import com.nearaid.core.network.dto.MeDto
import com.nearaid.core.network.dto.OtpRequestResponse
import com.nearaid.core.network.dto.PatchMeBody
import com.nearaid.core.network.dto.PublicUserDto
import com.nearaid.core.network.dto.RatingDto
import com.nearaid.core.network.dto.UserBriefDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthUserPrefsRepositoryTest {

    private val dispatcher = UnconfinedTestDispatcher()

    // --- Auth ------------------------------------------------------------------

    @Test
    fun requestOtp_maps_challenge() = runTest {
        val api = mockk<AuthApi>()
        val prefs = mockk<AuthPreferencesDataSource>(relaxed = true)
        coEvery { api.requestOtp(any()) } returns OtpRequestResponse(requestId = "req-1", expiresIn = 90)

        val result = AuthRepositoryImpl(api, prefs, dispatcher).requestOtp("+8801")

        assertTrue(result is DataResult.Success)
        val challenge = (result as DataResult.Success).data
        assertEquals("req-1", challenge.requestId)
        assertEquals(90, challenge.expiresInSeconds)
    }

    @Test
    fun verifyOtp_persists_session_and_returns_it() = runTest {
        val api = mockk<AuthApi>()
        val prefs = mockk<AuthPreferencesDataSource>(relaxed = true)
        coEvery { api.verifyOtp(any()) } returns AuthResponse(
            accessToken = "acc",
            refreshToken = "ref",
            isNewUser = true,
            user = UserBriefDto(id = "u1"),
        )

        val result = AuthRepositoryImpl(api, prefs, dispatcher).verifyOtp("req-1", "123456")

        assertTrue(result is DataResult.Success)
        val session = (result as DataResult.Success).data
        assertEquals("acc", session.accessToken)
        assertTrue(session.isNewUser)
        coVerify(exactly = 1) { prefs.saveSession(any(), "u1") }
    }

    @Test
    fun logout_clears_prefs_even_if_api_fails() = runTest {
        val api = mockk<AuthApi>()
        val prefs = mockk<AuthPreferencesDataSource>(relaxed = true)
        coEvery { api.logout() } throws RuntimeException("boom")

        AuthRepositoryImpl(api, prefs, dispatcher).logout()

        coVerify(exactly = 1) { prefs.clear() }
    }

    @Test
    fun isLoggedIn_exposes_the_prefs_stream() = runTest {
        val api = mockk<AuthApi>()
        val prefs = mockk<AuthPreferencesDataSource>()
        every { prefs.isLoggedIn } returns flowOf(true)

        assertTrue(AuthRepositoryImpl(api, prefs, dispatcher).isLoggedIn.first())
    }

    // --- User ------------------------------------------------------------------

    private fun meDto(name: String?) = MeDto(id = "u1", phone = "+8801", displayName = name, language = "en")

    @Test
    fun refreshMe_maps_and_publishes_to_the_observable() = runTest {
        val api = mockk<UserApi>()
        coEvery { api.getMe() } returns meDto("Ann")
        val repo = UserRepositoryImpl(api, dispatcher)

        val result = repo.refreshMe()

        assertTrue(result is DataResult.Success)
        assertEquals("Ann", (result as DataResult.Success).data.displayName)
        assertEquals("Ann", repo.observeMe().first()?.displayName)
    }

    @Test
    fun updateProfile_sends_language_code_and_caches_result() = runTest {
        val api = mockk<UserApi>()
        val slot = slot<PatchMeBody>()
        coEvery { api.updateMe(capture(slot)) } returns meDto("Bob")
        val repo = UserRepositoryImpl(api, dispatcher)

        repo.updateProfile(displayName = "Bob", language = AppLanguage.EN, photoUrl = null, defaultArea = null, email = null)

        assertEquals("Bob", slot.captured.displayName)
        assertEquals("en", slot.captured.language) // AppLanguage -> wire code
        assertEquals("Bob", repo.observeMe().first()?.displayName)
    }

    @Test
    fun getUserRatings_maps_the_page() = runTest {
        val api = mockk<UserApi>()
        coEvery { api.getRatings("u1", null) } returns RatingsPage(
            results = listOf(RatingDto(id = "r1", score = 5)),
            nextCursor = "c2",
            hasMore = true,
        )
        val result = UserRepositoryImpl(api, dispatcher).getUserRatings("u1")

        assertTrue(result is DataResult.Success)
        val page = (result as DataResult.Success).data
        assertEquals(5, page.items.single().score)
        assertEquals("c2", page.nextCursor)
    }

    @Test
    fun getPublicUser_and_registerDevice_delegate() = runTest {
        val api = mockk<UserApi>(relaxUnitFun = true)
        coEvery { api.getPublicUser("u9") } returns PublicUserDto(id = "u9")
        val repo = UserRepositoryImpl(api, dispatcher)

        assertEquals("u9", ((repo.getPublicUser("u9")) as DataResult.Success).data.id)
        assertTrue(repo.registerDevice("fcm") is DataResult.Success)
        coVerify(exactly = 1) { api.registerDevice(any()) }
    }

    // --- Preferences -----------------------------------------------------------

    @Test
    fun preferences_repository_reads_and_writes_through_the_data_source() = runTest {
        val prefs = mockk<UserPreferencesDataSource>(relaxed = true)
        every { prefs.language } returns flowOf(AppLanguage.BN)
        every { prefs.searchRadiusKm } returns flowOf(7.0)
        val repo = PreferencesRepositoryImpl(prefs)

        assertEquals(AppLanguage.BN, repo.language.first())
        assertEquals(7.0, repo.searchRadiusKm.first(), 0.0)
        repo.setLanguage(AppLanguage.EN)
        repo.setSearchRadiusKm(3.0)

        coVerify(exactly = 1) { prefs.setLanguage(AppLanguage.EN) }
        coVerify(exactly = 1) { prefs.setSearchRadiusKm(3.0) }
    }
}
