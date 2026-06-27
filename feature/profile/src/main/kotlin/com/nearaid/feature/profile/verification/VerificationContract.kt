package com.nearaid.feature.profile.verification

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState

data class VerificationState(
    val documentPath: String? = null,
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface VerificationIntent : UiIntent {
    data class DocumentPicked(val path: String) : VerificationIntent
    data object Submit : VerificationIntent
    data object BackClicked : VerificationIntent
}

sealed interface VerificationEffect : UiEffect {
    data object NavigateBack : VerificationEffect
}
