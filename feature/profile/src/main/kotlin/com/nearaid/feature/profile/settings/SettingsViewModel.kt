package com.nearaid.feature.profile.settings

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.domain.usecase.LogoutUseCase
import com.nearaid.core.domain.usecase.ObserveLanguageUseCase
import com.nearaid.core.domain.usecase.SetLanguageUseCase
import com.nearaid.core.domain.usecase.UpdateProfileUseCase
import com.nearaid.core.model.AppLanguage
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val logout: LogoutUseCase,
    private val observeLanguage: ObserveLanguageUseCase,
    private val setLanguage: SetLanguageUseCase,
    private val updateProfile: UpdateProfileUseCase,
) : MviViewModel<SettingsState, SettingsIntent, SettingsEffect>() {

    override fun initialState(): SettingsState = SettingsState()

    init {
        observeLanguageChanges()
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SelectLanguage -> selectLanguage(intent.lang)
            SettingsIntent.LogOut -> logOut()
            SettingsIntent.BackClicked -> sendEffect(SettingsEffect.NavigateBack)
        }
    }

    private fun observeLanguageChanges() {
        viewModelScope.launch {
            observeLanguage().collect { lang ->
                setState { copy(language = lang) }
            }
        }
    }

    private fun selectLanguage(lang: AppLanguage) {
        viewModelScope.launch {
            setLanguage(lang)
            updateProfile(language = lang)
        }
    }

    private fun logOut() {
        setState { copy(loading = true, error = null) }
        viewModelScope.launch {
            logout()
            setState { copy(loading = false) }
            sendEffect(SettingsEffect.LoggedOut)
        }
    }
}
