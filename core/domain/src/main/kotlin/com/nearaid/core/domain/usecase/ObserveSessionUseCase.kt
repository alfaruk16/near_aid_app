package com.nearaid.core.domain.usecase

import com.nearaid.core.model.Me
import com.nearaid.core.domain.repository.AuthRepository
import com.nearaid.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/** High-level auth/session state that drives the root navigation graph. */
sealed interface SessionState {
    data object LoggedOut : SessionState
    /** Authenticated but profile not yet set up (no display name) — FR-3. */
    data class NeedsProfile(val me: Me) : SessionState
    data class Ready(val me: Me) : SessionState
    data object Loading : SessionState
}

class ObserveSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<SessionState> =
        combine(authRepository.isLoggedIn, userRepository.observeMe()) { loggedIn, me ->
            when {
                !loggedIn -> SessionState.LoggedOut
                me == null -> SessionState.Loading
                me.displayName.isNullOrBlank() -> SessionState.NeedsProfile(me)
                else -> SessionState.Ready(me)
            }
        }
}
