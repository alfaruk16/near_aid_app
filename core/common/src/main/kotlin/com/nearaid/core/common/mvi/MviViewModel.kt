package com.nearaid.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base class for all feature ViewModels following the MVI pattern.
 *
 * - Holds a single source of truth [state] ([StateFlow]).
 * - Receives user [Intent]s through [onIntent].
 * - Emits one-off [Effect]s through [effect].
 *
 * Subclasses implement [initialState] and [onIntent]; they mutate state via
 * [setState] and fire navigation/snackbar events via [sendEffect].
 */
abstract class MviViewModel<State : UiState, Intent : UiIntent, Effect : UiEffect> : ViewModel() {

    private val _state: MutableStateFlow<State> by lazy { MutableStateFlow(initialState()) }
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    /** The state the screen starts in before any data loads. */
    protected abstract fun initialState(): State

    /** Reduce a user intent into state changes and/or effects. */
    abstract fun onIntent(intent: Intent)

    protected val currentState: State get() = _state.value

    /** Functional state update — `setState { copy(loading = true) }`. */
    protected fun setState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
