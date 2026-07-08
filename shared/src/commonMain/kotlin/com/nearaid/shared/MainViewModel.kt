package com.nearaid.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearaid.core.domain.usecase.ObserveLoginStateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Drives the app's start destination. `null` = still resolving (keep the splash up),
 * `true`/`false` = decided. Shared by the Android app and the iOS `MainViewController`.
 */
class MainViewModel(
    observeLoginState: ObserveLoginStateUseCase,
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean?> = observeLoginState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}
