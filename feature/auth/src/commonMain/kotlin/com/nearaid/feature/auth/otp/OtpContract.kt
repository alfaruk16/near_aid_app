package com.nearaid.feature.auth.otp

import com.nearaid.core.common.mvi.UiEffect
import com.nearaid.core.common.mvi.UiIntent
import com.nearaid.core.common.mvi.UiState

data class OtpState(
    val code: String = "",
    val loading: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface OtpIntent : UiIntent {
    data class CodeChanged(val value: String) : OtpIntent
    data class Verify(val requestId: String) : OtpIntent
}

sealed interface OtpEffect : UiEffect {
    data class Verified(val isNewUser: Boolean) : OtpEffect
}
