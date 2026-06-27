package com.nearaid.feature.auth.profilesetup

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val updateProfile: UpdateProfileUseCase,
) : MviViewModel<ProfileSetupState, ProfileSetupIntent, ProfileSetupEffect>() {

    override fun initialState() = ProfileSetupState()

    override fun onIntent(intent: ProfileSetupIntent) {
        when (intent) {
            is ProfileSetupIntent.NameChanged -> setState { copy(name = intent.value, error = null) }
            is ProfileSetupIntent.LanguageChanged -> setState { copy(language = intent.language) }
            ProfileSetupIntent.Finish -> finish()
        }
    }

    private fun finish() {
        val name = currentState.name.trim()
        if (name.isBlank()) {
            setState { copy(error = "Please enter a display name") }
            return
        }
        setState { copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val result = updateProfile(displayName = name, language = currentState.language)) {
                is DataResult.Success -> {
                    setState { copy(loading = false) }
                    sendEffect(ProfileSetupEffect.Done)
                }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }
}
