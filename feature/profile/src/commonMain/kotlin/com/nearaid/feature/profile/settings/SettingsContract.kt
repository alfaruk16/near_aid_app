package com.nearaid.feature.profile.settings

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.model.AppLanguage

data class SettingsState(
    val language: AppLanguage = AppLanguage.BN,
    val loading: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface SettingsIntent : UiIntent {
    data class SelectLanguage(val lang: AppLanguage) : SettingsIntent
    data object LogOut : SettingsIntent
    data object BackClicked : SettingsIntent
}

sealed interface SettingsEffect : UiEffect {
    data object LoggedOut : SettingsEffect
    data object NavigateBack : SettingsEffect
}
