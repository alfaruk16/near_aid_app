package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.repository.AuthRepository
import com.nearaid.core.domain.repository.UserRepository
import com.nearaid.core.model.AccountStatus
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Me
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveSessionUseCaseTest {

    private val auth = mockk<AuthRepository>()
    private val user = mockk<UserRepository>()

    private fun me(displayName: String?) = Me(
        id = "u1",
        phone = "+8801",
        email = null,
        displayName = displayName,
        photoUrl = null,
        language = AppLanguage.BN,
        defaultArea = null,
        isPhoneVerified = true,
        isIdVerified = false,
        trustScore = 50.0,
        status = AccountStatus.ACTIVE,
    )

    private suspend fun state(loggedIn: Boolean, me: Me?): SessionState {
        every { auth.isLoggedIn } returns flowOf(loggedIn)
        every { user.observeMe() } returns flowOf(me)
        return ObserveSessionUseCase(auth, user)().first()
    }

    @Test
    fun loggedOut_when_not_authenticated() = runTest {
        assertEquals(SessionState.LoggedOut, state(loggedIn = false, me = null))
    }

    @Test
    fun loading_when_authenticated_but_profile_not_loaded() = runTest {
        assertEquals(SessionState.Loading, state(loggedIn = true, me = null))
    }

    @Test
    fun needsProfile_when_display_name_blank() = runTest {
        val s = state(loggedIn = true, me = me(displayName = "  "))
        assertTrue(s is SessionState.NeedsProfile)
    }

    @Test
    fun ready_when_profile_complete() = runTest {
        val s = state(loggedIn = true, me = me(displayName = "Ann"))
        assertTrue(s is SessionState.Ready)
        assertEquals("Ann", (s as SessionState.Ready).me.displayName)
    }
}
