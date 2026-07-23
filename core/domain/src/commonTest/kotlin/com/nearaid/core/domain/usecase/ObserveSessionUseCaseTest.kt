package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.FakeAuthRepository
import com.nearaid.core.domain.FakeUserRepository
import com.nearaid.core.domain.sampleMe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObserveSessionUseCaseTest {

    private fun useCase(auth: FakeAuthRepository, user: FakeUserRepository) =
        ObserveSessionUseCase(auth, user)

    @Test
    fun logged_out_emits_LoggedOut_regardless_of_profile() = runTest {
        val auth = FakeAuthRepository().apply { isLoggedIn.value = false }
        val user = FakeUserRepository().apply { me.value = sampleMe() }
        assertEquals(SessionState.LoggedOut, useCase(auth, user).invoke().first())
    }

    @Test
    fun logged_in_with_no_profile_loaded_emits_Loading() = runTest {
        val auth = FakeAuthRepository().apply { isLoggedIn.value = true }
        val user = FakeUserRepository().apply { me.value = null }
        assertEquals(SessionState.Loading, useCase(auth, user).invoke().first())
    }

    @Test
    fun logged_in_with_blank_display_name_needs_profile() = runTest {
        val auth = FakeAuthRepository().apply { isLoggedIn.value = true }
        val user = FakeUserRepository().apply { me.value = sampleMe(displayName = "   ") }
        val state = useCase(auth, user).invoke().first()
        assertIs<SessionState.NeedsProfile>(state)
    }

    @Test
    fun logged_in_with_null_display_name_needs_profile() = runTest {
        val auth = FakeAuthRepository().apply { isLoggedIn.value = true }
        val user = FakeUserRepository().apply { me.value = sampleMe(displayName = null) }
        assertIs<SessionState.NeedsProfile>(useCase(auth, user).invoke().first())
    }

    @Test
    fun logged_in_with_display_name_is_Ready() = runTest {
        val auth = FakeAuthRepository().apply { isLoggedIn.value = true }
        val me = sampleMe(displayName = "Rahim")
        val user = FakeUserRepository().apply { this.me.value = me }
        val state = useCase(auth, user).invoke().first()
        assertIs<SessionState.Ready>(state)
        assertEquals(me, state.me)
    }
}
