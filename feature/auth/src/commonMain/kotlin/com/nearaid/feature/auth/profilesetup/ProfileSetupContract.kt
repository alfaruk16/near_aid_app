package com.nearaid.feature.auth.profilesetup

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.model.AppLanguage

data class ProfileSetupState(
    val name: String = "",
    val language: AppLanguage = AppLanguage.BN,
    val loading: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface ProfileSetupIntent : UiIntent {
    data class NameChanged(val value: String) : ProfileSetupIntent
    data class LanguageChanged(val language: AppLanguage) : ProfileSetupIntent
    data object Finish : ProfileSetupIntent
}

sealed interface ProfileSetupEffect : UiEffect {
    data object Done : ProfileSetupEffect
}
