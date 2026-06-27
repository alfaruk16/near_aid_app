package com.nearaid.feature.profile.profile

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.ObserveCurrentUserUseCase
import com.nearaid.core.domain.usecase.RefreshCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val refreshCurrentUser: RefreshCurrentUserUseCase,
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>() {

    override fun initialState(): ProfileState = ProfileState()

    init {
        observeMe()
        refreshMe()
    }

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.VerificationClicked -> sendEffect(ProfileEffect.OpenVerification)
            ProfileIntent.SettingsClicked -> sendEffect(ProfileEffect.OpenSettings)
            ProfileIntent.RatingsClicked -> {
                val userId = currentState.me?.id ?: return
                sendEffect(ProfileEffect.OpenPublicProfile(userId))
            }
        }
    }

    private fun observeMe() {
        viewModelScope.launch {
            observeCurrentUser().collect { me ->
                setState { copy(me = me, loading = me == null && loading) }
            }
        }
    }

    private fun refreshMe() {
        viewModelScope.launch {
            when (val result = refreshCurrentUser()) {
                is DataResult.Success -> setState { copy(loading = false, error = null) }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }
}
