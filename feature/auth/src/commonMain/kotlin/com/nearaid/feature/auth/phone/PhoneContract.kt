package com.nearaid.feature.auth.phone

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState
import com.nearaid.core.domain.usecase.PhoneNumber

data class PhoneState(
    val phone: String = "",
    val loading: Boolean = false,
    val error: String? = null,
) : UiState {
    val canSubmit: Boolean get() = PhoneNumber.isValidBd(phone) && !loading
}

sealed interface PhoneIntent : UiIntent {
    data class PhoneChanged(val value: String) : PhoneIntent
    data object Submit : PhoneIntent
}

sealed interface PhoneEffect : UiEffect {
    data class CodeSent(val requestId: String, val e164: String) : PhoneEffect
}
