package com.nearaid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearaid.core.domain.usecase.ObserveLoginStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Drives the app's start destination. `null` = still resolving (keep the splash up),
 * `true`/`false` = decided.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    observeLoginState: ObserveLoginStateUseCase,
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean?> = observeLoginState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}
