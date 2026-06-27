package com.nearaid.feature.auth.otp

import androidx.lifecycle.viewModelScope
import com.nearaid.core.common.mvi.MviViewModel
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.RefreshCurrentUserUseCase
import com.nearaid.core.domain.usecase.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtp: VerifyOtpUseCase,
    private val refreshCurrentUser: RefreshCurrentUserUseCase,
) : MviViewModel<OtpState, OtpIntent, OtpEffect>() {

    override fun initialState() = OtpState()

    override fun onIntent(intent: OtpIntent) {
        when (intent) {
            is OtpIntent.CodeChanged ->
                setState { copy(code = intent.value.filter { it.isDigit() }.take(6), error = null) }
            is OtpIntent.Verify -> verify(intent.requestId)
        }
    }

    private fun verify(requestId: String) {
        if (currentState.code.length < 6) return
        setState { copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val result = verifyOtp(requestId, currentState.code)) {
                is DataResult.Success -> {
                    refreshCurrentUser()
                    setState { copy(loading = false) }
                    sendEffect(OtpEffect.Verified(result.data.isNewUser))
                }
                is DataResult.Failure -> setState { copy(loading = false, error = result.error.message) }
            }
        }
    }
}
