package com.nearaid.feature.profile.profile

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.model.Me

data class ProfileState(
    val me: Me? = null,
    val loading: Boolean = true,
    val error: String? = null,
) : UiState

sealed interface ProfileIntent : UiIntent {
    data object VerificationClicked : ProfileIntent
    data object SettingsClicked : ProfileIntent
    data object RatingsClicked : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data object OpenVerification : ProfileEffect
    data object OpenSettings : ProfileEffect
    data class OpenPublicProfile(val userId: String) : ProfileEffect
}
