package com.nearaid.core.common.mvi

/**
 * MVI contract markers. Every screen defines its own:
 *  - [UiState]  : an immutable snapshot of everything the screen renders.
 *  - [UiIntent] : a user action / input event sent *into* the ViewModel.
 *  - [UiEffect] : a one-off side effect emitted *out* (navigation, snackbars).
 */
interface UiState

interface UiIntent

interface UiEffect
